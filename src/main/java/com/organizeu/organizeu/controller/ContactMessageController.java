package com.organizeu.organizeu.controller;

import com.organizeu.organizeu.model.ContactMessage;
import com.organizeu.organizeu.service.ContactMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
public class ContactMessageController {
    @Autowired
    private ContactMessageService contactMessageService;

    @PostMapping
    public ContactMessage sendMessage(@RequestBody ContactMessage message) {
        return contactMessageService.saveMessage(message.getName(), message.getEmail(), message.getMessage());
    }
} 