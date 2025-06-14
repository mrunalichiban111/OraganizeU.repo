package com.organizeu.organizeu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@PropertySource(value = "classpath:.env", ignoreResourceNotFound = true)
public class EnvConfig {
    private static final Logger logger = LoggerFactory.getLogger(EnvConfig.class);

    @Autowired
    private Environment env;

    @PostConstruct
    public void init() {
        logger.info("Initializing environment configuration");
        
        String clientId = env.getProperty("GOOGLE_CLIENT_ID");
        String clientSecret = env.getProperty("GOOGLE_CLIENT_SECRET");

        if (clientId == null || clientId.trim().isEmpty()) {
            logger.error("GOOGLE_CLIENT_ID is not set in .env file");
            throw new IllegalStateException("GOOGLE_CLIENT_ID is required but not set");
        }

        if (clientSecret == null || clientSecret.trim().isEmpty()) {
            logger.error("GOOGLE_CLIENT_SECRET is not set in .env file");
            throw new IllegalStateException("GOOGLE_CLIENT_SECRET is required but not set");
        }

        logger.info("Found Google OAuth2 credentials - Client ID: {}..., Client Secret: {}...", 
                   clientId.substring(0, 5), 
                   clientSecret.substring(0, 5));

        System.setProperty("spring.security.oauth2.client.registration.google.client-id", clientId);
        System.setProperty("spring.security.oauth2.client.registration.google.client-secret", clientSecret);
        logger.info("Successfully set Google OAuth2 credentials as system properties");
    }
} 