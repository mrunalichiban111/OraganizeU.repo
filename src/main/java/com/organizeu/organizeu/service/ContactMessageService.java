package com.organizeu.organizeu.service;

import com.organizeu.organizeu.model.ContactMessage;
import com.organizeu.organizeu.repository.ContactMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ContactMessageService {
    @Autowired
    private ContactMessageRepository contactMessageRepository;

    public ContactMessage saveMessage(String name, String email, String message) {
        ContactMessage contactMessage = new ContactMessage();
        contactMessage.setName(name);
        contactMessage.setEmail(email);
        contactMessage.setMessage(message);
        contactMessage.setDateSent(LocalDateTime.now());
        return contactMessageRepository.save(contactMessage);
    }
} 