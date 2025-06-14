package com.organizeu.organizeu.service.impl;

import com.organizeu.organizeu.dto.CalendarEventDTO;
import com.organizeu.organizeu.model.CalendarEvent;
import com.organizeu.organizeu.model.EventStatus;
import com.organizeu.organizeu.model.User;
import com.organizeu.organizeu.repository.CalendarEventRepository;
import com.organizeu.organizeu.service.CalendarEventService;
import com.organizeu.organizeu.service.UserActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CalendarEventServiceImpl implements CalendarEventService {
    @Autowired
    private CalendarEventRepository eventRepository;
    
    @Autowired
    private UserActivityService userActivityService;

    private CalendarEventDTO toDTO(CalendarEvent event) {
        CalendarEventDTO dto = new CalendarEventDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setLocation(event.getLocation());
        dto.setStartAt(event.getStartAt());
        dto.setEndAt(event.getEndAt());
        dto.setAllDay(event.isAllDay());
        dto.setRecurrenceRule(event.getRecurrenceRule());
        dto.setRecurrenceEnd(event.getRecurrenceEnd());
        dto.setStatus(event.getStatus() != null ? event.getStatus().name() : null);
        dto.setOwnerId(event.getOwner() != null ? event.getOwner().getId() : null);
        return dto;
    }

    private CalendarEvent fromDTO(CalendarEventDTO dto, User owner) {
        CalendarEvent event = new CalendarEvent();
        event.setId(dto.getId());
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());
        event.setStartAt(dto.getStartAt());
        event.setEndAt(dto.getEndAt());
        event.setAllDay(dto.isAllDay());
        event.setRecurrenceRule(dto.getRecurrenceRule());
        event.setRecurrenceEnd(dto.getRecurrenceEnd());
        event.setStatus(dto.getStatus() != null ? EventStatus.valueOf(dto.getStatus()) : null);
        event.setOwner(owner);
        return event;
    }

    private List<CalendarEventDTO> expandRecurring(CalendarEvent event, LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        List<CalendarEventDTO> instances = new ArrayList<>();
        if (event.getRecurrenceRule() == null) {
            instances.add(toDTO(event));
            return instances;
        }
        // For demo: only support daily recurrence
        LocalDateTime current = event.getStartAt();
        LocalDateTime until = event.getRecurrenceEnd() != null ? event.getRecurrenceEnd() : rangeEnd;
        while (!current.isAfter(until) && !current.isAfter(rangeEnd)) {
            if (!current.isBefore(rangeStart)) {
                CalendarEventDTO dto = toDTO(event);
                dto.setStartAt(current);
                dto.setEndAt(current.plusMinutes(
                    java.time.Duration.between(event.getStartAt(), event.getEndAt()).toMinutes()
                ));
                instances.add(dto);
            }
            // Only daily for demo; parse RRULE for more
            current = current.plusDays(1);
        }
        return instances;
    }

    @Override
    public List<CalendarEventDTO> getEventsForUserInRange(User user, LocalDate start, LocalDate end) {
        LocalDateTime startDt = start.atStartOfDay();
        LocalDateTime endDt = end.atTime(23, 59, 59);
        
        System.out.println("Fetching events for user " + user.getId() + " from " + startDt + " to " + endDt);
        
        // First get all events for the user to debug
        List<CalendarEvent> allEvents = eventRepository.findByOwner(user);
        System.out.println("All events for user " + user.getId() + ":");
        for (CalendarEvent event : allEvents) {
            System.out.println("Event: " + event.getTitle() + 
                             ", Start: " + event.getStartAt() + 
                             ", End: " + event.getEndAt() + 
                             ", Recurring: " + (event.getRecurrenceRule() != null) +
                             ", Owner: " + (event.getOwner() != null ? event.getOwner().getId() : "null"));
            
            // Add detailed date comparison logging
            System.out.println("Date comparison for " + event.getTitle() + ":");
            System.out.println("  Start date: " + event.getStartAt().toLocalDate());
            System.out.println("  End date: " + event.getEndAt().toLocalDate());
            System.out.println("  Target date: " + start);
            System.out.println("  Overlaps range: " + (event.getStartAt().isBefore(endDt) && event.getEndAt().isAfter(startDt)));
            System.out.println("  Starts on target: " + event.getStartAt().toLocalDate().equals(start));
            System.out.println("  Ends on target: " + event.getEndAt().toLocalDate().equals(start));
        }
        
        // Then get events in the specified range
        List<CalendarEvent> events = eventRepository.findByOwnerAndRangeOrRecurring(user, startDt, endDt);
        System.out.println("Found " + events.size() + " events in range");
        
        List<CalendarEventDTO> result = new ArrayList<>();
        for (CalendarEvent event : events) {
            System.out.println("Event in range: " + event.getTitle() + 
                             ", Start: " + event.getStartAt() + 
                             ", End: " + event.getEndAt() +
                             ", Owner: " + (event.getOwner() != null ? event.getOwner().getId() : "null"));
            if (event.getRecurrenceRule() != null) {
                List<CalendarEventDTO> expanded = expandRecurring(event, startDt, endDt);
                System.out.println("Expanded recurring event into " + expanded.size() + " instances");
                result.addAll(expanded);
            } else {
                result.add(toDTO(event));
            }
        }
        
        System.out.println("Returning " + result.size() + " events total");
        return result;
    }

    @Override
    public CalendarEventDTO createEvent(CalendarEventDTO dto, User user) {
        CalendarEvent event = fromDTO(dto, user);
        CalendarEvent saved = eventRepository.save(event);
        userActivityService.logActivity(user, "Added Event", "Added event: " + event.getTitle() + " (" + event.getStartAt() + ")");
        return toDTO(saved);
    }

    @Override
    public CalendarEventDTO updateEvent(Long id, CalendarEventDTO dto, User user) {
        CalendarEvent event = eventRepository.findById(id).orElseThrow();
        if (!event.getOwner().getId().equals(user.getId())) throw new RuntimeException("Unauthorized");
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());
        event.setStartAt(dto.getStartAt());
        event.setEndAt(dto.getEndAt());
        event.setAllDay(dto.isAllDay());
        event.setRecurrenceRule(dto.getRecurrenceRule());
        event.setRecurrenceEnd(dto.getRecurrenceEnd());
        event.setStatus(dto.getStatus() != null ? EventStatus.valueOf(dto.getStatus()) : null);
        return toDTO(eventRepository.save(event));
    }

    @Override
    public void deleteEvent(Long id, User user) {
        CalendarEvent event = eventRepository.findById(id).orElseThrow();
        if (!event.getOwner().getId().equals(user.getId())) throw new RuntimeException("Unauthorized");
        eventRepository.delete(event);
        userActivityService.logActivity(user, "Deleted Event", "Deleted event: " + event.getTitle() + " (" + event.getStartAt() + ")");
    }

    @Override
    public CalendarEventDTO getEvent(Long id, User user) {
        CalendarEvent event = eventRepository.findById(id).orElseThrow();
        if (!event.getOwner().getId().equals(user.getId())) throw new RuntimeException("Unauthorized");
        return toDTO(event);
    }

    @Override
    public List<CalendarEvent> findByOwner(User owner) {
        return eventRepository.findByOwner(owner);
    }

    @Override
    public List<CalendarEvent> findByOwnerAndStartAtBetween(User owner, LocalDateTime start, LocalDateTime end) {
        return eventRepository.findByOwnerAndRangeOrRecurring(owner, start, end);
    }

    @Override
    public Optional<CalendarEvent> findById(Long id) {
        return eventRepository.findById(id);
    }

    @Override
    public List<CalendarEvent> getEventsForUserInRange(User user, LocalDateTime start, LocalDateTime end) {
        return eventRepository.findByOwnerAndRangeOrRecurring(user, start, end);
    }

    @Override
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    @Override
    public List<CalendarEvent> getEventsForDateRange(User user, LocalDateTime start, LocalDateTime end) {
        return eventRepository.findByOwnerAndRangeOrRecurring(user, start, end);
    }

    @Override
    public CalendarEvent saveEvent(CalendarEvent event) {
        return eventRepository.save(event);
    }

    @Override
    public long countByOwner(User owner) {
        return eventRepository.countByOwner(owner);
    }
} 