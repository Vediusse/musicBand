package com.example.Service;

import Entities.Events.MusicEvent;


import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface EventService {

    @Async("taskExecutor")
    CompletableFuture<Optional<MusicEvent>> getEventById(Long id);

    @Async("taskExecutor")
    CompletableFuture<List<MusicEvent>> getAllEvents();

    @Async("taskExecutor")
    CompletableFuture<MusicEvent> createEvent(MusicEvent band);

    @Async("taskExecutor")
    CompletableFuture<MusicEvent> editEvent(Long id, MusicEvent bandUpdates);




}
