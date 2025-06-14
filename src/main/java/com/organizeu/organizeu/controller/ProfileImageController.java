package com.organizeu.organizeu.controller;

import com.organizeu.organizeu.service.ProfileImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile-image")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProfileImageController {

    @Autowired
    private ProfileImageService profileImageService;

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getProfileImage(@PathVariable String filename) {
        return profileImageService.serveProfileImage(filename);
    }
} 