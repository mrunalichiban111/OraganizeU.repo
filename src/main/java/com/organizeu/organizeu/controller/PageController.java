package com.organizeu.organizeu.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;

@Controller
public class PageController implements ErrorController {
    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/aboutus")
    public String aboutUs() {
        return "aboutus";
    }

    @GetMapping("/calendar")
    public String calendar() {
        return "calendar";
    }

    @GetMapping("/tasks")
    public String tasks() {
        return "tasks";
    }

    @GetMapping("/notes")
    public String notes(Model model, @AuthenticationPrincipal Object principal) {
        model.addAttribute("user", principal);
        return "notes";
    }

    @GetMapping("/access-denied")
    public String accessDenied(Model model, @AuthenticationPrincipal Object principal) {
        model.addAttribute("user", principal);
        return "access-denied";
    }

    @RequestMapping("/error")
    public String handleError(Model model, @AuthenticationPrincipal Object principal) {
        model.addAttribute("user", principal);
        // This will render the error.html page
        return "error";
    }
}