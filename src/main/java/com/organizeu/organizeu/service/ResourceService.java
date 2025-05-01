package com.organizeu.organizeu.service;

import com.organizeu.organizeu.model.Section;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResourceService {

    private final List<Section> sections = new ArrayList<>();
    private Section selectedSection = null;

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(String name) {
        if (name == null || name.trim().isEmpty()) return;

        String trimmedName = name.trim();


        for (Section section : sections) {
            if (section.getName().equalsIgnoreCase(trimmedName)) {
                selectedSection = section;
                return;
            }
        }

        Section newSection = new Section(trimmedName);
        sections.add(newSection);
        selectedSection = newSection;
    }

    public Section getSelectedSection() {
        return selectedSection;
    }

    public void selectSection(String name) {
        if (name == null || name.trim().isEmpty()) return;

        for (Section section : sections) {
            if (section.getName().equalsIgnoreCase(name.trim())) {
                selectedSection = section;
                return;
            }
        }
    }

    public boolean hasSections() {
        return !sections.isEmpty();
    }
}

