package com.organizeu.organizeu.controller;

import com.organizeu.organizeu.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserDashboardController {

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        User user = new User();
        user.setName("Guest");
        user.setEmail("guest@organizeu.com");
        user.setPicture("/assets/default-profile.svg");
        user.setJoinDate(java.time.LocalDateTime.now());
        model.addAttribute("user", user);
        model.addAttribute("totalEvents", 0);
        model.addAttribute("totalResources", 0);
        model.addAttribute("totalTasks", 0);
        return "user/dashboard";
    }

    @GetMapping("/schedule")
    public String showSchedulePage(Model model) {
        User user = new User();
        user.setName("Guest");
        user.setEmail("guest@organizeu.com");
        user.setPicture("/assets/default-profile.svg");
        user.setJoinDate(java.time.LocalDateTime.now());
        model.addAttribute("user", user);
        return "schedule";
    }
}