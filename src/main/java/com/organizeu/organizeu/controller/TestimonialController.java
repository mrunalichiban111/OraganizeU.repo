package com.organizeu.organizeu.controller;

import com.organizeu.organizeu.model.Testimonial;
import com.organizeu.organizeu.service.TestimonialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/testimonials")
public class TestimonialController {
    @Autowired
    private TestimonialService testimonialService;

    @GetMapping
    public List<Testimonial> getAllTestimonials() {
        return testimonialService.getAllTestimonials();
    }

    @GetMapping("/me")
    public Optional<Testimonial> getMyTestimonial(Principal principal) {
        return testimonialService.getTestimonialByUserName(principal.getName());
    }

    @PostMapping
    public Testimonial addOrUpdateTestimonial(@RequestBody Testimonial testimonial, Principal principal) {
        return testimonialService.addOrUpdateTestimonial(principal.getName(), testimonial.getMessage());
    }
} 