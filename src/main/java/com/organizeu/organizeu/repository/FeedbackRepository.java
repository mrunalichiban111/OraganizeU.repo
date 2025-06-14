package com.organizeu.organizeu.repository;

import com.organizeu.organizeu.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
} 