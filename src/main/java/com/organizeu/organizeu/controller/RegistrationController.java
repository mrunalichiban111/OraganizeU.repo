package com.organizeu.organizeu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RegistrationController {
    @GetMapping("/register")
    public String showRegistrationPage() {
        return "register";
    }
} 