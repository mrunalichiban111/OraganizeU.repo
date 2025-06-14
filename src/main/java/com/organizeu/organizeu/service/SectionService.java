package com.organizeu.organizeu.service;

import com.organizeu.organizeu.model.Section;
import com.organizeu.organizeu.model.User;
import com.organizeu.organizeu.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SectionService {

    @Autowired
    private SectionRepository sectionRepository;

    public List<Section> getAllSections(User user) {
        return sectionRepository.findByUser(user);
    }

    public Optional<Section> getSectionByName(String name, User user) {
        return sectionRepository.findByNameAndUser(name, user);
    }

    @Transactional
    public Section createSection(String name, User user) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Section name cannot be empty");
        }

        String trimmedName = name.trim();
        if (sectionRepository.existsByNameAndUser(trimmedName, user)) {
            throw new RuntimeException("Section already exists: " + trimmedName);
        }

        Section section = new Section();
        section.setName(trimmedName);
        section.setUser(user);
        return sectionRepository.save(section);
    }

    @Transactional
    public void deleteSection(String name, User user) {
        Section section = sectionRepository.findByNameAndUser(name, user)
                .orElseThrow(() -> new RuntimeException("Section not found: " + name));
        sectionRepository.delete(section);
    }
}

