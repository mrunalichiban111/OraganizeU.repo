package com.organizeu.organizeu.repository;

import com.organizeu.organizeu.model.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
} 