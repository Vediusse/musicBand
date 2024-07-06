package com.example.Service;


import Entities.Bands.Band;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface BandService {
    @Async("taskExecutor")
    CompletableFuture<Band> createBand(Band band);

    @Async("taskExecutor")
    CompletableFuture<Band> editBand(Long id, Band bandUpdates);

    @Async("taskExecutor")
    CompletableFuture<Optional<Band>> getBandById(Long id);

    @Async("taskExecutor")
    CompletableFuture<List<Band>> getAllBand();

    @Async("taskExecutor")
    CompletableFuture<String> registerBandToEvent(Long idBand, Long idEvent);

}
