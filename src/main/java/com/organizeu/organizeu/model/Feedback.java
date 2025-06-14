package com.organizeu.organizeu.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Boolean firstVisit;

    @Column(nullable = false)
    private Boolean foundWanted;

    @Column(nullable = false)
    private String satisfaction;

    @Column(nullable = false)
    private String returnLikelihood;

    @Column(length = 2000)
    private String comments;

    @Column(nullable = false)
    private LocalDateTime dateSubmitted;
} 