package com.organizeu.organizeu.repository;

import com.organizeu.organizeu.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStartAtBetween(LocalDateTime start, LocalDateTime end);
} 