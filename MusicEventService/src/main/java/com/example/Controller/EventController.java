package com.example.Controller;


import Entities.Events.MusicEvent;
import com.example.Service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<String>> createBand(@RequestBody MusicEvent band) {
        return eventService.createEvent(band)
                .thenApply(savedBand -> ResponseEntity.ok("Event created successfully with ID: "));
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<List<MusicEvent>>> getAllBand() {
        return eventService.getAllEvents()
                .thenApply(savedBands -> {
                    if (savedBands.isEmpty()) {
                        return ResponseEntity.noContent().build();
                    } else {
                        return ResponseEntity.ok(savedBands);
                    }
                });
    }

    @PatchMapping("/{id}")
    public CompletableFuture<ResponseEntity<String>> editBand(@PathVariable Long id, @RequestBody MusicEvent bandUpdates) {
        return eventService.editEvent(id, bandUpdates)
                .thenApply(updatedBand -> {
                    if (updatedBand != null) {
                        return ResponseEntity.ok("Band updated successfully: " + updatedBand.getName());
                    } else {
                        return ResponseEntity.notFound().build();
                    }
                });
    }


    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<String>> getBand(@PathVariable Long id) {
        return eventService.getEventById(id)
                .thenApply(savedBand -> {
                    if (savedBand.isPresent()) {
                        MusicEvent band = savedBand.get();
                        return ResponseEntity.ok("Band found with ID: " );
                    } else {
                        return ResponseEntity.notFound().build();
                    }
                });
    }
}
