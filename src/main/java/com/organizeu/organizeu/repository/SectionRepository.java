package com.organizeu.organizeu.repository;

import com.organizeu.organizeu.model.Section;
import com.organizeu.organizeu.model.User;
import com.organizeu.organizeu.model.LinkResource;
import com.organizeu.organizeu.model.FileResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findByUser(User user);
    Optional<Section> findByNameAndUser(String name, User user);
    boolean existsByNameAndUser(String name, User user);
    Optional<LinkResource> findLinkByIdAndUser(Long linkId, User user);
    Optional<FileResource> findFileByIdAndUser(Long fileId, User user);
} 