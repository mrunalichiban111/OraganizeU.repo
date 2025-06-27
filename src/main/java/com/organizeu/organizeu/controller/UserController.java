package com.organizeu.organizeu.controller;

import com.organizeu.organizeu.model.User;
import com.organizeu.organizeu.service.UserService;
import com.organizeu.organizeu.service.ProfileImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ProfileImageService profileImageService;

    @GetMapping("/profile")
    public String showProfile(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getAttribute("email");
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);
        return "user/profile";
    }

    @GetMapping("/settings")
    public String showSettings(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getAttribute("email");
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);
        return "user/settings";
    }

    @GetMapping("/profile-image")
    public ResponseEntity<Resource> getProfileImage(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            logger.warn("Profile image request received without authentication");
            return ResponseEntity.notFound().build();
        }

        String email = principal.getAttribute("email");
        logger.info("Fetching profile image for user with email: {}", email);

        User user = userService.findByEmail(email).orElse(null);
        if (user == null) {
            logger.warn("User not found for email: {}", email);
            return ResponseEntity.notFound().build();
        }

        if (user.getPicture() == null) {
            logger.warn("No profile picture URL found for user: {}", email);
            return ResponseEntity.notFound().build();
        }

        try {
            // Extract filename from the picture URL
            String filename = user.getPicture().substring(user.getPicture().lastIndexOf("/") + 1);
            logger.info("Serving profile image with filename: {}", filename);
            return profileImageService.serveProfileImage(filename);
        } catch (Exception e) {
            logger.error("Error serving profile image for user: {}. Error: {}", email, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/api/profile-image/{filename}")
    public ResponseEntity<Resource> serveProfileImage(@PathVariable String filename) {
        logger.info("Serving profile image with filename: {}", filename);
        return profileImageService.serveProfileImage(filename);
    }
}