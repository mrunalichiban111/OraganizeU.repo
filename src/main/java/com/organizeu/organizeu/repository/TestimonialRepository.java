package com.organizeu.organizeu.repository;

import com.organizeu.organizeu.model.Testimonial;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TestimonialRepository extends JpaRepository<Testimonial, Long> {
    Optional<Testimonial> findByUserName(String userName);
    boolean existsByUserName(String userName);
} 