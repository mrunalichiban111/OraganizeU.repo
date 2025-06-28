package com.organizeu.organizeu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.InputStream;

@RestController
public class DebugController {
    @GetMapping("/debug/templates")
    public ResponseEntity<String> checkTemplates() {
        try {
            InputStream is = getClass().getResourceAsStream("/templates/schedule.html");
            if (is != null) {
                return ResponseEntity.ok("Template exists");
            }
            return ResponseEntity.status(404).body("schedule.html not found in templates/");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
