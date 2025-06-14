package com.organizeu.organizeu.service;

import com.organizeu.organizeu.model.User;
import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email);
    User registerUser(User user);
    User saveUser(User user);
    User findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
} 