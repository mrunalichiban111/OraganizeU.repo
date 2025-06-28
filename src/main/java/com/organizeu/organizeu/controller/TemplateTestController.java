package com.organizeu.organizeu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TemplateTestController {
    @GetMapping("/test-template")
    public String testTemplate() {
        return "schedule";
    }
}
