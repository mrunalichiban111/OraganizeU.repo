package com.organizeu.organizeu.service;

import com.organizeu.organizeu.model.Feedback;
import com.organizeu.organizeu.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;

    public Feedback saveFeedback(String name, String email, Boolean firstVisit, Boolean foundWanted, String satisfaction, String returnLikelihood, String comments) {
        Feedback feedback = new Feedback();
        feedback.setName(name);
        feedback.setEmail(email);
        feedback.setFirstVisit(firstVisit);
        feedback.setFoundWanted(foundWanted);
        feedback.setSatisfaction(satisfaction);
        feedback.setReturnLikelihood(returnLikelihood);
        feedback.setComments(comments);
        feedback.setDateSubmitted(LocalDateTime.now());
        return feedbackRepository.save(feedback);
    }
} 