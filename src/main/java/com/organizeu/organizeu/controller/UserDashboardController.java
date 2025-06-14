package com.organizeu.organizeu.controller;

import com.organizeu.organizeu.model.CalendarEvent;
import com.organizeu.organizeu.model.Resource;
import com.organizeu.organizeu.model.User;
import com.organizeu.organizeu.model.UserActivity;
import com.organizeu.organizeu.service.CalendarEventService;
import com.organizeu.organizeu.service.ResourceService;
import com.organizeu.organizeu.service.UserActivityService;
import com.organizeu.organizeu.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserDashboardController {
    private static final Logger logger = LoggerFactory.getLogger(UserDashboardController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private CalendarEventService eventService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private UserActivityService activityService;

    @GetMapping("/dashboard")
    public String showDashboard(@AuthenticationPrincipal OAuth2User principal, Model model) {
        logger.info("Loading dashboard for user: {}", principal != null ? principal.getAttribute("email") : "unknown");
        
        try {
            if (principal == null) {
                logger.warn("No authenticated user found");
                return "redirect:/login";
            }

            String email = principal.getAttribute("email");
            logger.info("Looking up user with email: {}", email);
            
            Optional<User> userOpt = userService.findByEmail(email);
            
            if (userOpt.isEmpty()) {
                logger.warn("User not found for email: {}", email);
                return "redirect:/login";
            }

            User user = userOpt.get();
            logger.info("Found user: {} with picture URL: {}", user.getEmail(), user.getPicture());
            
            // Get today's date range
            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
            
            // Fetch all events for today
            List<CalendarEvent> todayEvents = eventService.findByOwnerAndStartAtBetween(user, startOfDay, endOfDay);
            logger.info("Found {} events for today", todayEvents.size());
            
            // Get total events count
            long totalEvents = eventService.countByOwner(user);
            logger.info("Total events for user: {}", totalEvents);
            
            // Get recent resources (last 5)
            List<Resource> recentResources = resourceService.findByOwnerOrderByCreatedAtDesc(user);
            logger.info("Found {} recent resources", recentResources.size());
            
            // Get total resources count
            long totalResources = resourceService.countByOwner(user);
            logger.info("Total resources for user: {}", totalResources);
            
            // Get recent activities (last 10)
            List<UserActivity> recentActivities = activityService.findByUserOrderByTimestampDesc(user);
            logger.info("Found {} recent activities", recentActivities.size());
            
            // Add all data to the model
            model.addAttribute("user", user);
            model.addAttribute("todayEvents", todayEvents);
            model.addAttribute("recentResources", recentResources);
            model.addAttribute("recentActivities", recentActivities);
            model.addAttribute("totalEvents", totalEvents);
            model.addAttribute("totalResources", totalResources);
            model.addAttribute("totalTasks", 0); // TODO: Implement task counting
            
            return "user/dashboard";
            
        } catch (Exception e) {
            logger.error("Error loading dashboard: {}", e.getMessage(), e);
            return "redirect:/error";
        }
    }
} 