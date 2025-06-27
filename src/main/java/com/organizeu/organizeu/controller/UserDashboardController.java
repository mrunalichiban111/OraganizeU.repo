package com.organizeu.organizeu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserDashboardController {

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // No authentication or user lookup
        // No user-specific data, just show dashboard
        return "user/dashboard";
    }

    @GetMapping("/schedule")
    public String showSchedulePage(Model model) {
        // No authentication or user lookup
        return "schedule";
    }
}