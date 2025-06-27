package com.organizeu.organizeu.controller;

import com.organizeu.organizeu.model.User;
import com.organizeu.organizeu.service.ProfileImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private ProfileImageService profileImageService;


    @GetMapping("/profile")
    public String showProfile(Model model, @AuthenticationPrincipal Object principal) {
        model.addAttribute("user", getUserFromPrincipal(principal));
        return "user/profile";
    }

    @GetMapping("/settings")
    public String showSettings(Model model, @AuthenticationPrincipal Object principal) {
        model.addAttribute("user", getUserFromPrincipal(principal));
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