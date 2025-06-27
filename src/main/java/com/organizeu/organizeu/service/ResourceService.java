package com.organizeu.organizeu.service;

import com.organizeu.organizeu.model.Resource;
import com.organizeu.organizeu.model.Section;
import com.organizeu.organizeu.model.User;
import java.util.List;
import java.util.Optional;

public interface ResourceService {
    List<Resource> getRecentResources(User user, int limit);
    long getTotalResources(User user);
    Resource saveResource(Resource resource);
    void deleteResource(Long id);
    Optional<Resource> findById(Long id);
    
    // Section management
    List<Section> getAllSections(User user);
    Section getSectionByName(String name, User user);
    void addSection(String name, User user);
    void selectSection(String name);
    
    // Link management
    void addLink(String title, String url, String sectionName, User user);
    void deleteLink(Long linkId, User user);
    
    // File management
    void addFile(String title, String fileName, String filePath, String sectionName, User user);
    void deleteFile(Long fileId, User user);

    List<Resource> findByOwner(User owner);
    long countByOwner(User owner);
    List<Resource> findTop3ByOwnerOrderByCreatedAtDesc(User owner);
    List<Resource> findByOwnerOrderByCreatedAtDesc(User owner);
    List<Resource> findAll();
}