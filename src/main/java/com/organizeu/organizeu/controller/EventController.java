package com.organizeu.organizeu.controller;

import com.organizeu.organizeu.dto.CalendarEventDTO;
import com.organizeu.organizeu.model.CalendarEvent;
import com.organizeu.organizeu.model.EventStatus;
import com.organizeu.organizeu.model.User;
import com.organizeu.organizeu.service.CalendarEventService;
import com.organizeu.organizeu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class EventController {

    @Autowired
    private CalendarEventService calendarEventService;

    @Autowired
    private UserService userService;

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
            @AuthenticationPrincipal OAuth2User principal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String end) {
        
        try {
            if (principal == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not authenticated"));
            }

            String email = principal.getAttribute("email");
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            LocalDateTime startDateTime = parseDateTime(start);
            LocalDateTime endDateTime = parseDateTime(end);

            List<CalendarEvent> events = calendarEventService.getEventsForDateRange(user, startDateTime, endDateTime);
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
    public String showCalendar(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getAttribute("email");
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);
        return "calendar";
    }

    @PostMapping("/events")
    @ResponseBody
    public ResponseEntity<?> createEvent(
            @AuthenticationPrincipal OAuth2User principal,
            @RequestBody CalendarEventDTO eventDTO) {
        
        try {
            if (principal == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not authenticated"));
            }

            String email = principal.getAttribute("email");
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

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

            CalendarEventDTO savedEvent = calendarEventService.createEvent(eventDTO, user);
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
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable Long id,
            @RequestBody CalendarEventDTO eventDTO) {
        
        try {
            if (principal == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not authenticated"));
            }

            String email = principal.getAttribute("email");
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Verify ownership
            CalendarEvent existingEvent = calendarEventService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Event not found"));
            if (!existingEvent.getOwner().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "Not authorized to update this event"));
            }

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

            CalendarEventDTO updatedEvent = calendarEventService.updateEvent(id, eventDTO, user);
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
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable Long id) {
        
        try {
            if (principal == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not authenticated"));
            }

            String email = principal.getAttribute("email");
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Verify ownership
            CalendarEvent event = calendarEventService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Event not found"));
            if (!event.getOwner().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "Not authorized to delete this event"));
            }

            calendarEventService.deleteEvent(id, user);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
} 