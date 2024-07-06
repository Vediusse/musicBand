package com.example.Repository;

import Entities.Events.EventBand;
import Entities.Events.EventBandId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventBandRepository extends JpaRepository<EventBand, Long> {
}
