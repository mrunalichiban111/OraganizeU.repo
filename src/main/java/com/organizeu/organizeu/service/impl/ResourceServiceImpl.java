package com.organizeu.organizeu.service.impl;

import com.organizeu.organizeu.model.*;
import com.organizeu.organizeu.repository.ResourceRepository;
import com.organizeu.organizeu.repository.SectionRepository;
import com.organizeu.organizeu.service.ResourceService;
import com.organizeu.organizeu.service.UserActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private UserActivityService userActivityService;

    @Override
    public List<Resource> getRecentResources(User user, int limit) {
        return resourceRepository.findTop3ByOwnerOrderByCreatedAtDesc(user);
    }

    @Override
    public long getTotalResources(User user) {
        return resourceRepository.countByOwner(user);
    }

    @Override
    public Resource saveResource(Resource resource) {
        Resource saved = resourceRepository.save(resource);
        userActivityService.logActivity(resource.getOwner(), "Added Resource", "Added resource: " + resource.getTitle());
        return saved;
    }

    @Override
    public void deleteResource(Long id) {
        Resource resource = resourceRepository.findById(id).orElse(null);
        if (resource != null) {
            userActivityService.logActivity(resource.getOwner(), "Deleted Resource", "Deleted resource: " + resource.getTitle());
        }
        resourceRepository.deleteById(id);
    }

    @Override
    public Optional<Resource> findById(Long id) {
        return resourceRepository.findById(id);
    }

    @Override
    public List<Section> getAllSections(User user) {
        return sectionRepository.findByUser(user);
    }

    @Override
    public Section getSectionByName(String name, User user) {
        return sectionRepository.findByNameAndUser(name, user)
                .orElseThrow(() -> new RuntimeException("Section not found: " + name));
    }

    @Override
    @Transactional
    public void addSection(String name, User user) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Section name cannot be empty");
        }

        String trimmedName = name.trim();
        if (sectionRepository.existsByNameAndUser(trimmedName, user)) {
            throw new RuntimeException("Section already exists: " + trimmedName);
        }

        Section newSection = new Section();
        newSection.setName(trimmedName);
        newSection.setUser(user);
        sectionRepository.save(newSection);
    }

    @Override
    public void selectSection(String name) {
        // This method is kept for backward compatibility
        // The section selection is now handled through the URL parameter
    }

    @Override
    @Transactional
    public void addLink(String title, String url, String sectionName, User user) {
        Section section = getSectionByName(sectionName, user);
        LinkResource link = new LinkResource();
        link.setTitle(title);
        link.setUrl(url);
        link.setSection(section);
        section.addLink(link);
        sectionRepository.save(section);
        userActivityService.logActivity(user, "Added Resource", "Added link: " + title + " in section: " + sectionName);
    }

    @Override
    @Transactional
    public void deleteLink(Long linkId, User user) {
        LinkResource link = sectionRepository.findLinkByIdAndUser(linkId, user)
                .orElseThrow(() -> new RuntimeException("Link not found with id: " + linkId));
        Section section = link.getSection();
        section.removeLink(link);
        sectionRepository.save(section);
    }

    @Override
    @Transactional
    public void addFile(String title, String fileName, String filePath, String sectionName, User user) {
        Section section = getSectionByName(sectionName, user);
        FileResource file = new FileResource();
        file.setTitle(title);
        file.setName(fileName);
        file.setUrl(filePath);
        file.setSection(section);
        section.addFile(file);
        sectionRepository.save(section);
        userActivityService.logActivity(user, "Added Resource", "Added file: " + title + " in section: " + sectionName);
    }

    @Override
    @Transactional
    public void deleteFile(Long fileId, User user) {
        FileResource file = sectionRepository.findFileByIdAndUser(fileId, user)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + fileId));
        Section section = file.getSection();
        section.removeFile(file);
        sectionRepository.save(section);
    }

    @Override
    public List<Resource> findByOwner(User owner) {
        return resourceRepository.findByOwner(owner);
    }

    @Override
    public long countByOwner(User owner) {
        return resourceRepository.countByOwner(owner);
    }

    @Override
    public List<Resource> findTop3ByOwnerOrderByCreatedAtDesc(User owner) {
        return resourceRepository.findTop3ByOwnerOrderByCreatedAtDesc(owner);
    }

    @Override
    public List<Resource> findByOwnerOrderByCreatedAtDesc(User owner) {
        return resourceRepository.findByOwnerOrderByCreatedAtDesc(owner);
    }

    @Override
    public List<Resource> findAll() {
        return resourceRepository.findAll();
    }
}