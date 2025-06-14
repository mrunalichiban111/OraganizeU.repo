package com.organizeu.organizeu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Service
public class ProfileImageService {
    private static final Logger logger = LoggerFactory.getLogger(ProfileImageService.class);
    private final Path profileImageDir;

    @Autowired
    private RestTemplate restTemplate;

    public ProfileImageService() {
        this.profileImageDir = Paths.get("profile-images").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.profileImageDir);
            logger.info("Profile image directory created at: {}", this.profileImageDir);
        } catch (IOException ex) {
            logger.error("Could not create profile image directory", ex);
            throw new RuntimeException("Could not create profile image directory", ex);
        }
    }

    public String getProfileImageUrl(String googleImageUrl) {
        if (googleImageUrl == null || googleImageUrl.isEmpty()) {
            logger.info("No Google image URL provided, using default profile image");
            return "/assets/default-profile.svg";
        }

        try {
            // Generate a unique filename based on the Google URL
            String filename = Base64.getUrlEncoder().encodeToString(googleImageUrl.getBytes());
            Path imagePath = profileImageDir.resolve(filename + ".jpg");

            // If image doesn't exist locally, download it
            if (!Files.exists(imagePath)) {
                logger.info("Downloading profile image from: {}", googleImageUrl);
                
                // Create headers with user agent and referrer policy
                HttpHeaders headers = new HttpHeaders();
                headers.set("User-Agent", "Mozilla/5.0");
                headers.set("Referer", "https://accounts.google.com/");
                HttpEntity<String> entity = new HttpEntity<>(headers);

                // Download the image
                byte[] imageBytes = restTemplate.execute(
                    googleImageUrl,
                    HttpMethod.GET,
                    request -> {
                        request.getHeaders().addAll(headers);
                    },
                    response -> {
                        if (response.getStatusCode() == HttpStatus.OK) {
                            return response.getBody().readAllBytes();
                        }
                        logger.error("Failed to download image: {}", response.getStatusCode());
                        throw new IOException("Failed to download image: " + response.getStatusCode());
                    }
                );

                if (imageBytes != null && imageBytes.length > 0) {
                    Files.write(imagePath, imageBytes);
                    logger.info("Successfully cached profile image: {}", imagePath);
                } else {
                    logger.error("Failed to download profile image: empty response");
                    return "/assets/default-profile.svg";
                }
            } else {
                logger.info("Using cached profile image: {}", imagePath);
            }

            // Return the local URL
            return "/user/api/profile-image/" + filename;
        } catch (Exception e) {
            logger.error("Error processing profile image: {}", e.getMessage(), e);
            return "/assets/default-profile.svg";
        }
    }

    public ResponseEntity<Resource> serveProfileImage(String filename) {
        try {
            Path imagePath = profileImageDir.resolve(filename + ".jpg");
            Resource resource = new UrlResource(imagePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                logger.info("Serving profile image: {}", imagePath);
                return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .header(HttpHeaders.CACHE_CONTROL, "max-age=31536000") // Cache for 1 year
                    .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                    .body(resource);
            } else {
                logger.warn("Profile image not found: {}", imagePath);
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            logger.error("Error serving profile image: {}", e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    public String downloadAndEncodeImage(String imageUrl) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<byte[]> response = restTemplate.exchange(
                new URI(imageUrl),
                HttpMethod.GET,
                entity,
                byte[].class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                outputStream.write(response.getBody());
                return Base64.getEncoder().encodeToString(outputStream.toByteArray());
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
} 