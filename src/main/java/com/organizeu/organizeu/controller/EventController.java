package com.organizeu.organizeu.controller;

import com.organizeu.organizeu.dto.CalendarEventDTO;
import com.organizeu.organizeu.model.CalendarEvent;
import com.organizeu.organizeu.service.CalendarEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class EventController {

    @Autowired
    private CalendarEventService calendarEventService;

    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Kolkata");

    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            // First try parsing as ISO-8601
            Instant instant = Instant.parse(dateTimeStr);
            return LocalDateTime.ofInstant(instant, ZONE_ID);
        } catch (DateTimeParseException e) {
            // If that fails, try parsing as local date time
            try {
                return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException e2) {
                throw new DateTimeParseException("Could not parse date: " + dateTimeStr, dateTimeStr, 0);
            }
        }
    }

    private CalendarEventDTO convertToDTO(CalendarEvent event) {
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

    @GetMapping("/events")
    @ResponseBody
    public ResponseEntity<?> getEvents(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String end) {
        try {
            LocalDateTime startDateTime = parseDateTime(start);
            LocalDateTime endDateTime = parseDateTime(end);

            // Instead, fetch all events for the date range (no user filtering)
            List<CalendarEvent> events = calendarEventService.getEventsForDateRange(null, startDateTime, endDateTime);
            List<CalendarEventDTO> dtos = events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/calendar")
    public String showCalendar(Model model) {
        // REMOVED: if (principal == null) { ... }
        // REMOVED: User lookup by principal
        // Just return the calendar view
        return "calendar";
    }

    @PostMapping("/events")
    @ResponseBody
    public ResponseEntity<?> createEvent(
            @RequestBody CalendarEventDTO eventDTO) {
        try {
            if (eventDTO.getStartAt() != null) {
                LocalDateTime startDateTime = parseDateTime(eventDTO.getStartAt().toString());
                eventDTO.setStartAt(startDateTime);
            }
            if (eventDTO.getEndAt() != null) {
                LocalDateTime endDateTime = parseDateTime(eventDTO.getEndAt().toString());
                eventDTO.setEndAt(endDateTime);
            }

            // Validate event times
            if (eventDTO.getStartAt() != null && eventDTO.getEndAt() != null) {
                if (eventDTO.getStartAt().isAfter(eventDTO.getEndAt())) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Start time must be before end time"));
                }
            }

            CalendarEventDTO savedEvent = calendarEventService.createEvent(eventDTO, null);
            return ResponseEntity.ok(savedEvent);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/events/{id}")
    @ResponseBody
    public ResponseEntity<?> updateEvent(
            @PathVariable Long id,
            @RequestBody CalendarEventDTO eventDTO) {
        try {
            // REMOVED: if (principal == null) { ... }
            // REMOVED: User lookup by principal
            // REMOVED: Ownership check
            if (eventDTO.getStartAt() != null) {
                LocalDateTime startDateTime = parseDateTime(eventDTO.getStartAt().toString());
                eventDTO.setStartAt(startDateTime);
            }
            if (eventDTO.getEndAt() != null) {
                LocalDateTime endDateTime = parseDateTime(eventDTO.getEndAt().toString());
                eventDTO.setEndAt(endDateTime);
            }

            // Validate event times
            if (eventDTO.getStartAt() != null && eventDTO.getEndAt() != null) {
                if (eventDTO.getStartAt().isAfter(eventDTO.getEndAt())) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Start time must be before end time"));
                }
            }

            CalendarEventDTO updatedEvent = calendarEventService.updateEvent(id, eventDTO, null);
            return ResponseEntity.ok(updatedEvent);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/events/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteEvent(
            @PathVariable Long id) {
        try {
            // REMOVED: if (principal == null) { ... }
            // REMOVED: User lookup by principal
            // REMOVED: Ownership check
            calendarEventService.deleteEvent(id, null);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}