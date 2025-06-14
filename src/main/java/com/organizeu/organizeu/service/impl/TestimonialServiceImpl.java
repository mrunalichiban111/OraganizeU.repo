package com.organizeu.organizeu.service.impl;

import com.organizeu.organizeu.model.Testimonial;
import com.organizeu.organizeu.repository.TestimonialRepository;
import com.organizeu.organizeu.service.TestimonialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TestimonialServiceImpl implements TestimonialService {
    @Autowired
    private TestimonialRepository testimonialRepository;

    @Override
    public Testimonial addOrUpdateTestimonial(String userName, String message) {
        Optional<Testimonial> existing = testimonialRepository.findByUserName(userName);
        if (existing.isPresent()) {
            Testimonial t = existing.get();
            t.setMessage(message);
            t.setDateEdited(LocalDateTime.now());
            return testimonialRepository.save(t);
        } else {
            Testimonial t = new Testimonial();
            t.setUserName(userName);
            t.setMessage(message);
            t.setDatePosted(LocalDateTime.now());
            t.setDateEdited(null);
            return testimonialRepository.save(t);
        }
    }

    @Override
    public List<Testimonial> getAllTestimonials() {
        return testimonialRepository.findAll();
    }

    @Override
    public Optional<Testimonial> getTestimonialByUserName(String userName) {
        return testimonialRepository.findByUserName(userName);
    }
} 