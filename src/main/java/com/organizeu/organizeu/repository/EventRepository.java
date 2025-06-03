package com.organizeu.organizeu.repository;

import com.organizeu.organizeu.model.Event;
import com.organizeu.organizeu.model.EventStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByDateBetween(LocalDate start, LocalDate end);
    List<Event> findByDate(LocalDate date);
    List<Event> findByStatus(EventStatus status);
}