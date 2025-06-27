package com.organizeu.organizeu.controller;

import com.organizeu.organizeu.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserDashboardController {

    @GetMapping("/dashboard")
    public String showDashboard(Model model, @AuthenticationPrincipal Object principal) {
        User user = getUserFromPrincipal(principal);
        model.addAttribute("user", user);
        model.addAttribute("totalEvents", 0);
        model.addAttribute("totalResources", 0);
        model.addAttribute("totalTasks", 0);
        return "user/dashboard";
    }

    // Removed redundant /user/schedule mapping. Use /schedule from ScheduleController only.

    private User getUserFromPrincipal(Object principal) {
        if (principal == null) {
            User user = new User();
            user.setName("Guest");
            user.setEmail("guest@organizeu.com");
            user.setPicture("/assets/default-profile.svg");
            user.setJoinDate(java.time.LocalDateTime.now());
            return user;
        }
        if (principal instanceof OAuth2User oAuth2User) {
            User user = new User();
            user.setName((String) oAuth2User.getAttribute("name"));
            user.setEmail((String) oAuth2User.getAttribute("email"));
            user.setPicture((String) oAuth2User.getAttribute("picture"));
            user.setJoinDate(java.time.LocalDateTime.now());
            return user;
        }
        // fallback guest
        User user = new User();
        user.setName("Guest");
        user.setEmail("guest@organizeu.com");
        user.setPicture("/assets/default-profile.svg");
        user.setJoinDate(java.time.LocalDateTime.now());
        return user;
    }
}