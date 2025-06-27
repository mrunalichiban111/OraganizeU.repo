package com.organizeu.organizeu.controller;

import com.organizeu.organizeu.service.ProfileImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private ProfileImageService profileImageService;

    @GetMapping("/profile")
    public String showProfile(Model model) {
        // No authentication or user lookup
        return "user/profile";
    }

    @GetMapping("/settings")
    public String showSettings(Model model) {
        // No authentication or user lookup
        return "user/settings";
    }

    @GetMapping("/profile-image")
    public ResponseEntity<Resource> getProfileImage() {
        // No authentication or user lookup
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/api/profile-image/{filename}")
    public ResponseEntity<Resource> serveProfileImage(@PathVariable String filename) {
        return profileImageService.serveProfileImage(filename);
    }
}