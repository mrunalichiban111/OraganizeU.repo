package com.organizeu.organizeu.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String notes() {
        return "notes";
    }

    @RequestMapping("/error")
    public String handleError() {
        // This will render the error.html page
        return "error";
    }
} 