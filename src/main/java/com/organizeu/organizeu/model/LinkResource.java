package com.organizeu.organizeu.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "link_resources")
@Data
public class LinkResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String url;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;
}
