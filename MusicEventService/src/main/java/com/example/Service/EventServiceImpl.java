package com.example.Service;

import Entities.Events.EventBand;
import Entities.Events.EventBandId;
import Kafka.BandRegisteredEvent;
import Kafka.EventRegistrationResponse;
import Entities.Events.MusicEvent;
import com.example.Repository.EventBandRepository;
import com.example.Repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
@Service
public class EventServiceImpl implements EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);

    private final EventRepository eventRepository;
    private final KafkaTemplate<String, EventRegistrationResponse> eventResponseKafkaTemplate;
    private final EventBandRepository eventBandRepository;

    @Autowired
    public EventServiceImpl(KafkaTemplate<String, EventRegistrationResponse> eventResponseKafkaTemplate, EventRepository eventRepository, EventBandRepository eventBandRepository) {
        this.eventResponseKafkaTemplate = eventResponseKafkaTemplate;
        this.eventRepository = eventRepository;
        this.eventBandRepository = eventBandRepository;
    }

    @Transactional
    @Async("taskExecutor")
    public CompletableFuture<MusicEvent> createEvent(MusicEvent band) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return eventRepository.save(band);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create band: " + e.getMessage());
            }
        });
    }

    @Cacheable("get_events")
    @Transactional(readOnly = true)
    @Async("taskExecutor")
    public CompletableFuture<Optional<MusicEvent>> getEventById(Long id) {
        return CompletableFuture.completedFuture(eventRepository.findById(id));
    }

    @Transactional(readOnly = true)
    @Async("taskExecutor")
    public CompletableFuture<List<MusicEvent>> getAllEvents() {
        return CompletableFuture.completedFuture(eventRepository.findAll());
    }

    @Transactional
    @Async("taskExecutor")
    public CompletableFuture<MusicEvent> editEvent(Long id, MusicEvent bandUpdates) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<MusicEvent> bandOptional = eventRepository.findById(id);
            return bandOptional.map(band -> {
                // Обновление свойств band здесь
                return eventRepository.save(band);
            }).orElse(null);
        });
    }

    @KafkaListener(topics = "band-registration-request", groupId = "event-service-group", containerFactory = "bandRegisteredEventKafkaListenerContainerFactory")
    public void processBandRegistrationRequest(@Payload BandRegisteredEvent request) {
        try {
            if (request == null) {
                logger.error("Received null BandRegisteredEvent");
                return;
            }
            Long bandId = request.getBandId();
            Long eventId = request.getEventId();

            if (bandId == null || eventId == null) {
                logger.error("BandId or eventId is null in BandRegisteredEvent");
                return;
            }

            Optional<MusicEvent> eventOptional = eventRepository.findById(eventId);

            if (eventOptional.isPresent()) {
                MusicEvent event = eventOptional.get();
                CompletableFuture<Boolean> registrationResultFuture = checkBandRegistration(event);
                registrationResultFuture.thenAccept(canRegister -> {
                    if (canRegister) {
                        logger.info("Registration successful for bandId: {} for eventId: {}", bandId, eventId);
                        eventBandRepository.save(new EventBand(eventId, bandId));


                        synchronized (event) {
                            event.setBandsCount(event.getBandsCount() + 1);
                            eventRepository.save(event);
                        }

                        eventResponseKafkaTemplate.send("band-registration", new EventRegistrationResponse(eventId, bandId, true, request.getCorrelationId()));
                    } else {
                        logger.info("Registration unsuccessful for bandId: {} for eventId: {}", bandId, eventId);
                        eventResponseKafkaTemplate.send("band-registration", new EventRegistrationResponse(eventId, bandId, false, request.getCorrelationId()));
                    }
                }).exceptionally(ex -> {
                    logger.error("Exception occurred during registration for bandId: {} for eventId: {}", bandId, eventId, ex);
                    eventResponseKafkaTemplate.send("band-registration", new EventRegistrationResponse(eventId, bandId, false, request.getCorrelationId()));
                    return null;
                });
            } else {
                eventResponseKafkaTemplate.send("band-registration-failed", new EventRegistrationResponse(eventId, bandId, false, request.getCorrelationId()));
            }
        } catch (Exception e) {
            logger.error("Exception occurred while processing BandRegisteredEvent", e);
            eventResponseKafkaTemplate.send("band-registration-failed", new EventRegistrationResponse(request.getEventId(), request.getBandId(), false, request.getCorrelationId()));
        }
    }


    private CompletableFuture<Boolean> checkBandRegistration(MusicEvent event) {
        if(event.getBandsCount() < event.getCapability()){
            return CompletableFuture.completedFuture(true);
        }
        return CompletableFuture.completedFuture(false);
    }
}