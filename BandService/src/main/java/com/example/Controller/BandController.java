package com.example.Controller;

import Aspects.CheckPermission;
import Entities.Bands.Band;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import com.example.Service.BandService;

import java.util.List;

@RestController
@RequestMapping("/api/bands")
public class BandController {

    private final BandService bandService;

    public BandController(BandService bandService) {
        this.bandService = bandService;
    }

    @PostMapping
    public Mono<ResponseEntity<String>> createBand(@RequestBody Band band) {
        return Mono.fromFuture(() -> bandService.createBand(band))
                .map(savedBand -> ResponseEntity.ok("ЭЭЭ создалось " + savedBand.getId() +
                        ", Name: " + savedBand.getName() +
                        ", Genre: " + savedBand.getGenre()));
    }

    @GetMapping
    @CheckPermission("ADMIN")
    public Mono<ResponseEntity<List<Band>>> getAllBand() {
        return Mono.fromFuture(bandService::getAllBand)
                .map(savedBands -> {
                    if (savedBands.isEmpty()) {
                        return ResponseEntity.noContent().build();
                    } else {
                        return ResponseEntity.ok(savedBands);
                    }
                });
    }

    @PatchMapping("/{id}")
    public Mono<ResponseEntity<String>> editBand(@PathVariable Long id, @RequestBody Band bandUpdates) {
        return Mono.fromFuture(() -> bandService.editBand(id, bandUpdates))
                .map(updatedBand -> {
                    if (updatedBand != null) {
                        return ResponseEntity.ok("Обновилось хъ " + updatedBand.getName());
                    } else {
                        return ResponseEntity.notFound().build();
                    }
                });
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<String>> getBand(@PathVariable Long id) {
        return Mono.fromFuture(() -> bandService.getBandById(id))
                .map(savedBand -> {
                    if (savedBand.isPresent()) {
                        Band band = savedBand.get();
                        return ResponseEntity.ok("Ну вот а че хотел то " + band.getId() +
                                ", Name: " + band.getName() +
                                ", Genre: " + band.getGenre());
                    } else {
                        return ResponseEntity.notFound().build();
                    }
                });
    }

    @GetMapping("/{idBand}/register/{idEvent}")
    public Mono<ResponseEntity<String>> registerBand(@PathVariable Long idBand, @PathVariable Long idEvent) {
        return Mono.fromFuture(() -> bandService.registerBandToEvent(idBand, idEvent))
                .map(savedBand -> ResponseEntity.ok("Band registered to event successfully with ID: " + savedBand));
    }
}
