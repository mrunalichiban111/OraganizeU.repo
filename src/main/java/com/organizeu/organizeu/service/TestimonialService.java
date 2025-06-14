package com.organizeu.organizeu.service;

import com.organizeu.organizeu.model.Testimonial;
import java.util.List;
import java.util.Optional;

public interface TestimonialService {
    Testimonial addOrUpdateTestimonial(String userName, String message);
    List<Testimonial> getAllTestimonials();
    Optional<Testimonial> getTestimonialByUserName(String userName);
} 