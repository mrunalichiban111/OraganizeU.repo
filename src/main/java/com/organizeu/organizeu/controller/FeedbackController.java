package com.organizeu.organizeu.controller;

import com.organizeu.organizeu.model.Feedback;
import com.organizeu.organizeu.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public Feedback submitFeedback(@RequestBody Feedback feedback) {
        return feedbackService.saveFeedback(
            feedback.getName(),
            feedback.getEmail(),
            feedback.getFirstVisit(),
            feedback.getFoundWanted(),
            feedback.getSatisfaction(),
            feedback.getReturnLikelihood(),
            feedback.getComments()
        );
    }
} 