package com.organizeu.organizeu.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sections")
@Data
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LinkResource> links = new ArrayList<>();

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FileResource> files = new ArrayList<>();

    public void addLink(LinkResource link) {
        links.add(link);
        link.setSection(this);
    }

    public void addLink(String title, String url) {
        LinkResource link = new LinkResource();
        link.setTitle(title);
        link.setUrl(url);
        link.setSection(this);
        links.add(link);
    }

    public void removeLink(LinkResource link) {
        links.remove(link);
        link.setSection(null);
    }

    public void removeLinkById(String linkId) {
        links.removeIf(link -> link.getId().toString().equals(linkId));
    }

    public void addFile(FileResource file) {
        files.add(file);
        file.setSection(this);
    }

    public void addFile(String title, String name, String url) {
        FileResource file = new FileResource();
        file.setTitle(title);
        file.setName(name);
        file.setUrl(url);
        file.setSection(this);
        files.add(file);
    }

    public void removeFile(FileResource file) {
        files.remove(file);
        file.setSection(null);
    }

    public void removeFileById(String fileId) {
        files.removeIf(file -> file.getId().toString().equals(fileId));
    }
}
