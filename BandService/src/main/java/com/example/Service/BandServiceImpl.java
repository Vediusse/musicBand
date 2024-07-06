package com.example.Service;

import Kafka.BandRegisteredEvent;
import Entities.Bands.Band;
import Entities.Bands.Person;
import Kafka.EventRegistrationResponse;
import com.example.Repository.BandRepository;
import com.example.Repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BandServiceImpl implements BandService {

    private final BandRepository bandRepository;
    private final PersonRepository personRepository;
    private final KafkaTemplate<String, BandRegisteredEvent> eventResponseKafkaTemplate;

    private final ConcurrentHashMap<String, CompletableFuture<EventRegistrationResponse>> pendingResponses = new ConcurrentHashMap<>();


    @Autowired
    public BandServiceImpl(BandRepository bandRepository, PersonRepository personRepository, KafkaTemplate<String, BandRegisteredEvent> eventResponseKafkaTemplate) {
        this.bandRepository = bandRepository;
        this.personRepository = personRepository;
        this.eventResponseKafkaTemplate = eventResponseKafkaTemplate;
    }


    @Transactional
    @Async("taskExecutor")
    public CompletableFuture<Band> createBand(Band band) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (band.getFrontMan() != null && band.getFrontMan().getId() == null) {
                    Person savedFrontMan = personRepository.save(band.getFrontMan());
                    band.setFrontMan(savedFrontMan);
                }
                return bandRepository.save(band);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create band: " + e.getMessage());
            }
        });
    }


    @Transactional(readOnly = true)
    @Async("taskExecutor")
    public CompletableFuture<Optional<Band>> getBandById(Long id) {
        return CompletableFuture.completedFuture(bandRepository.findById(id));
    }

    @Cacheable("bands")
    @Transactional(readOnly = true)
    @Async("taskExecutor")
    public CompletableFuture<List<Band>> getAllBand() {
        return CompletableFuture.completedFuture(bandRepository.findAll());
    }

    @Async("taskExecutor")
    public CompletableFuture<String> registerBandToEvent(Long bandId, Long eventId) {
        String correlationId = UUID.randomUUID().toString();
        BandRegisteredEvent event = new BandRegisteredEvent(bandId, eventId, correlationId);
        CompletableFuture<EventRegistrationResponse> responseFuture = new CompletableFuture<>();
        pendingResponses.put(correlationId, responseFuture);

        // Отправляем сообщение через Kafka
        ListenableFuture<SendResult<String, BandRegisteredEvent>> future = eventResponseKafkaTemplate.send("band-registration-request", event);

        future.addCallback(new ListenableFutureCallback<SendResult<String, BandRegisteredEvent>>() {
            @Override
            public void onSuccess(SendResult<String, BandRegisteredEvent> result) {
                // Успешная отправка, ничего не делаем, т.к. future уже добавлен в pendingResponses
            }

            @Override
            public void onFailure(Throwable ex) {
                CompletableFuture<EventRegistrationResponse> failedFuture = pendingResponses.remove(correlationId);
                if (failedFuture != null) {
                    failedFuture.completeExceptionally(ex);
                }
            }
        });

        return responseFuture.thenApply(response -> {
            if (response.isSuccess()) {
                return "Band registered to event successfully with ID: " + response.isSuccess();
            } else {
                return "Band registration failed for event with ID: " + response.isSuccess();
            }
        }).exceptionally(ex -> "Failed to register band to event: " + ex.getMessage());
    }

    @KafkaListener(topics = "band-registration", groupId = "event-service-group", containerFactory = "bandResponseEventKafkaListenerContainerFactory")
    public void listen(EventRegistrationResponse response) {
        CompletableFuture<EventRegistrationResponse> responseFuture = pendingResponses.remove(response.getCorrelationId());

        if (responseFuture != null) {
            responseFuture.complete(response);
        }
    }



    @Cacheable("bands")
    @Transactional
    @Async("taskExecutor")
    public CompletableFuture<Band> editBand(Long id, Band bandUpdates) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Band> bandOptional = bandRepository.findById(id);
            if (bandOptional.isPresent()) {
                Band band = bandOptional.get();
                if (bandUpdates.getName() != null) {
                    band.setName(bandUpdates.getName());
                }
                if (bandUpdates.getGenre() != null) {
                    band.setGenre(bandUpdates.getGenre());
                }
                if (bandUpdates.getNumberOfParticipants() != null) {
                    band.setNumberOfParticipants(bandUpdates.getNumberOfParticipants());
                }
                if (bandUpdates.getSinglesCount() > 0) {
                    band.setSinglesCount(bandUpdates.getSinglesCount());
                }
                if (bandUpdates.getEstablishmentDate() != null) {
                    band.setEstablishmentDate(bandUpdates.getEstablishmentDate());
                }
                if (bandUpdates.getFrontMan() != null) {
                    band.setFrontMan(bandUpdates.getFrontMan());
                }
                return bandRepository.save(band);
            }
            return null;
        });
    }
}
