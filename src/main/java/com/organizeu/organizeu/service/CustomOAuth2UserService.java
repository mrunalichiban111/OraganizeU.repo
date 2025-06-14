package com.organizeu.organizeu.service;

import com.organizeu.organizeu.model.User;
import com.organizeu.organizeu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileImageService profileImageService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        logger.info("==== CustomOAuth2UserService.loadUser CALLED ====");
        logger.info("Provider: {}", userRequest.getClientRegistration().getRegistrationId());
        
        try {
            OAuth2User oauth2User = super.loadUser(userRequest);
            Map<String, Object> attributes = oauth2User.getAttributes();
            logger.info("Raw OAuth2 attributes: {}", attributes);
            
            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");
            String picture = oauth2User.getAttribute("picture");
            
            logger.info("Extracted attributes - Email: {}, Name: {}, Picture: {}", email, name, picture);
            
            if (email == null) {
                logger.error("Email attribute is null in OAuth2 response");
                throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
            }
            
            Optional<User> userOpt = userRepository.findByEmail(email);
            User user;
            
            if (userOpt.isEmpty()) {
                logger.info("Creating new user for email: {}", email);
                user = new User();
                user.setEmail(email);
                user.setName(name);
                user.setUsername(email.split("@")[0]);
                user.setPassword("");
                user.setJoinDate(LocalDateTime.now());
                user.setPicture(profileImageService.getProfileImageUrl(picture));
                
                try {
                    user = userRepository.save(user);
                    logger.info("Successfully created new user - ID: {}, Email: {}, Picture: {}", 
                              user.getId(), user.getEmail(), user.getPicture());
                } catch (Exception e) {
                    logger.error("Failed to save new user: {}", e.getMessage(), e);
                    throw new OAuth2AuthenticationException("Failed to create user: " + e.getMessage());
                }
            } else {
                user = userOpt.get();
                logger.info("Found existing user - ID: {}, Email: {}, Current Picture: {}", 
                          user.getId(), user.getEmail(), user.getPicture());
                
                // Update picture if it's different or null
                if (picture != null && (user.getPicture() == null || !picture.equals(user.getPicture()))) {
                    logger.info("Updating picture for user {} from {} to {}", 
                              user.getId(), user.getPicture(), picture);
                    user.setPicture(profileImageService.getProfileImageUrl(picture));
                    user = userRepository.save(user);
                }
            }
            
            // Create a new modifiable map from the original attributes
            Map<String, Object> modifiableAttributes = new HashMap<>(attributes);
            modifiableAttributes.put("id", user.getId());
            modifiableAttributes.put("username", user.getUsername());
            modifiableAttributes.put("name", user.getName());
            modifiableAttributes.put("picture", user.getPicture()); // Use cached image URL
            
            logger.info("Final OAuth2 attributes being returned: {}", modifiableAttributes);
            
            return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                modifiableAttributes,
                "email"
            );
            
        } catch (Exception e) {
            logger.error("Error processing OAuth2 user: {}", e.getMessage(), e);
            throw new OAuth2AuthenticationException("Error processing OAuth2 user: " + e.getMessage());
        }
    }
} 