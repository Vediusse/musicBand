package com.example.Repository;

import Entities.Events.MusicEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<MusicEvent, Long> {
}
