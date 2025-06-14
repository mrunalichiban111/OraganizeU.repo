package com.organizeu.organizeu.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "file_resources")
@Data
public class FileResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String url;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;
}
