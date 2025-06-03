package com.organizeu.organizeu.service;

import com.organizeu.organizeu.model.Section;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SectionService {

    private final ResourceService resourceService;

    public SectionService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    public List<Section> getAllSections() {
        return resourceService.getSections();
    }


    public Section getSectionByName(String sectionName) {
        if (sectionName == null) {
            return null;
        }
        return resourceService.getSections()
                .stream()
                .filter(section -> section.getName().equalsIgnoreCase(sectionName))
                .findFirst()
                .orElse(null);
    }
}

