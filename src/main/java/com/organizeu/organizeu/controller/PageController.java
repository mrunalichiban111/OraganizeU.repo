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
    public String home(Model model, @AuthenticationPrincipal Object principal) {
        model.addAttribute("user", getUserFromPrincipal(principal));
        return "index";
    }

    private com.organizeu.organizeu.model.User getUserFromPrincipal(Object principal) {
        if (principal == null) {
            com.organizeu.organizeu.model.User user = new com.organizeu.organizeu.model.User();
            user.setName("Guest");
            user.setEmail("guest@organizeu.com");
            user.setPicture("/assets/default-profile.svg");
            user.setJoinDate(java.time.LocalDateTime.now());
            return user;
        }
        if (principal instanceof org.springframework.security.oauth2.core.user.OAuth2User oAuth2User) {
            com.organizeu.organizeu.model.User user = new com.organizeu.organizeu.model.User();
            user.setName((String) oAuth2User.getAttribute("name"));
            user.setEmail((String) oAuth2User.getAttribute("email"));
            user.setPicture((String) oAuth2User.getAttribute("picture"));
            user.setJoinDate(java.time.LocalDateTime.now());
            return user;
        }
        // fallback guest
        com.organizeu.organizeu.model.User user = new com.organizeu.organizeu.model.User();
        user.setName("Guest");
        user.setEmail("guest@organizeu.com");
        user.setPicture("/assets/default-profile.svg");
        user.setJoinDate(java.time.LocalDateTime.now());
        return user;
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