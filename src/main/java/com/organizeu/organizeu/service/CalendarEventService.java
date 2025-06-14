package com.organizeu.organizeu.service;

import com.organizeu.organizeu.dto.CalendarEventDTO;
import com.organizeu.organizeu.model.CalendarEvent;
import com.organizeu.organizeu.model.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CalendarEventService {
    List<CalendarEventDTO> getEventsForUserInRange(User user, LocalDate start, LocalDate end);
    CalendarEventDTO createEvent(CalendarEventDTO dto, User user);
    CalendarEventDTO updateEvent(Long id, CalendarEventDTO dto, User user);
    void deleteEvent(Long id, User user);
    CalendarEventDTO getEvent(Long id, User user);
    List<CalendarEvent> findByOwnerAndStartAtBetween(User owner, LocalDateTime start, LocalDateTime end);
    List<CalendarEvent> getEventsForDateRange(User user, LocalDateTime start, LocalDateTime end);
    CalendarEvent saveEvent(CalendarEvent event);
    void deleteEvent(Long id);
    List<CalendarEvent> getEventsForUserInRange(User user, LocalDateTime start, LocalDateTime end);
    Optional<CalendarEvent> findById(Long id);
    List<CalendarEvent> findByOwner(User owner);
    long countByOwner(User owner);
} 