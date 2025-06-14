package com.organizeu.organizeu.service.impl;

import com.organizeu.organizeu.model.User;
import com.organizeu.organizeu.repository.UserRepository;
import com.organizeu.organizeu.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        logger.info("Looking up user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public User registerUser(User user) {
        logger.info("Attempting to register new user with email: {}", user.getEmail());
        if (userRepository.existsByEmail(user.getEmail())) {
            logger.error("Registration failed - Email already exists: {}", user.getEmail());
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            logger.error("Registration failed - Username already exists: {}", user.getUsername());
            throw new RuntimeException("Username already exists");
        }
        
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        User saved = userRepository.save(user);
        logger.info("Successfully registered new user - ID: {}, Email: {}", saved.getId(), saved.getEmail());
        return saved;
    }

    @Override
    @Transactional
    public User saveUser(User user) {
        logger.info("Saving user - ID: {}, Email: {}", user.getId(), user.getEmail());
        User saved = userRepository.save(user);
        logger.info("Successfully saved user - ID: {}, Email: {}", saved.getId(), saved.getEmail());
        return saved;
    }

    @Override
    public User findByUsername(String username) {
        logger.info("Looking up user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User not found for username: {}", username);
                    return new RuntimeException("User not found");
                });
    }

    @Override
    public boolean existsByEmail(String email) {
        logger.info("Checking if user exists with email: {}", email);
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        logger.info("Checking if user exists with username: {}", username);
        return userRepository.existsByUsername(username);
    }
} 