package com.organizeu.organizeu.repository;

import com.organizeu.organizeu.model.Resource;
import com.organizeu.organizeu.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByOwner(User owner);
    List<Resource> findByOwnerOrderByCreatedAtDesc(User owner);
    List<Resource> findTop3ByOwnerOrderByCreatedAtDesc(User owner);
    long countByOwner(User owner);
} 