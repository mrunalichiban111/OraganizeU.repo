package com.organizeu.organizeu.service;

import com.organizeu.organizeu.model.Event;
import com.organizeu.organizeu.repository.EventRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getEventsBetween(LocalDate start, LocalDate end) {
        return eventRepository.findByDateBetween(start, end);
    }

    public Event createEvent(Event event) {
        if (event.getStartTime() != null && event.getEndTime() != null && event.getStartTime().isAfter(event.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        return eventRepository.save(event);
    }

    public Optional<Event> updateEvent(Long id, Event eventDetails) {
        return eventRepository.findById(id)
            .map(event -> {
                event.setTitle(eventDetails.getTitle());
                event.setDate(eventDetails.getDate());
                event.setStartTime(eventDetails.getStartTime());
                event.setEndTime(eventDetails.getEndTime());
                event.setStatus(eventDetails.getStatus());

                if (event.getStartTime() != null && event.getEndTime() != null && event.getStartTime().isAfter(event.getEndTime())) {
                    throw new IllegalArgumentException("Start time must be before end time");
                }

                return eventRepository.save(event);
            });
    }

    public boolean deleteEvent(Long id) {
        return eventRepository.findById(id)
            .map(event -> {
                eventRepository.delete(event);
                return true;
            })
            .orElse(false);
    }
}