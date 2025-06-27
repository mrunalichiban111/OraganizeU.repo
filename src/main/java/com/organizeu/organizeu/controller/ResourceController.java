package com.organizeu.organizeu.controller;

import com.organizeu.organizeu.model.Resource;
import com.organizeu.organizeu.model.User;
import com.organizeu.organizeu.service.ResourceService;
import com.organizeu.organizeu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/resources")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private UserService userService;

    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    @GetMapping
    public String listResources(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        String email = principal.getAttribute("email");
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        List<Resource> resources = resourceService.findByOwner(user);
        model.addAttribute("resources", resources);
        return "resource_management";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("resource", new Resource());
        return "resource_management";
    }

    @PostMapping
    public String createResource(@RequestParam("file") MultipartFile file,
                               @RequestParam("title") String title,
                               @RequestParam("description") String description,
                               @RequestParam("category") String category,
                               @AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        String email = principal.getAttribute("email");
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path targetLocation = fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation);

            Resource resource = new Resource();
            resource.setTitle(title);
            resource.setDescription(description);
            resource.setCategory(category);
            resource.setFilePath(fileName);
            resource.setCreatedAt(LocalDateTime.now());
            resource.setOwner(user);

            resourceService.saveResource(resource);
            return "redirect:/resources";
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @GetMapping("/{id}")
    public String viewResource(@PathVariable Long id, @AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        String email = principal.getAttribute("email");
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Resource resource = resourceService.findById(id).orElseThrow(() -> new RuntimeException("Resource not found"));
        
        if (!resource.getOwner().getId().equals(user.getId())) {
            return "redirect:/resources";
        }
        
        model.addAttribute("resource", resource);
        return "resource_management";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, @AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        String email = principal.getAttribute("email");
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Resource resource = resourceService.findById(id).orElseThrow(() -> new RuntimeException("Resource not found"));
        
        if (!resource.getOwner().getId().equals(user.getId())) {
            return "redirect:/resources";
        }
        
        model.addAttribute("resource", resource);
        return "resource_management";
    }

    @PostMapping("/{id}")
    public String updateResource(@PathVariable Long id,
                               @RequestParam("file") MultipartFile file,
                               @RequestParam("title") String title,
                               @RequestParam("description") String description,
                               @RequestParam("category") String category,
                               @AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        String email = principal.getAttribute("email");
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Resource resource = resourceService.findById(id).orElseThrow(() -> new RuntimeException("Resource not found"));
        
        if (!resource.getOwner().getId().equals(user.getId())) {
            return "redirect:/resources";
        }

        try {
            if (!file.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path targetLocation = fileStorageLocation.resolve(fileName);
                Files.copy(file.getInputStream(), targetLocation);
                resource.setFilePath(fileName);
            }

            resource.setTitle(title);
            resource.setDescription(description);
            resource.setCategory(category);
            resource.setOwner(user);

            resourceService.saveResource(resource);
            return "redirect:/resources";
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteResource(@PathVariable Long id, @AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        String email = principal.getAttribute("email");
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Resource resource = resourceService.findById(id).orElseThrow(() -> new RuntimeException("Resource not found"));
        
        if (!resource.getOwner().getId().equals(user.getId())) {
            return "redirect:/resources";
        }
        
        resourceService.deleteResource(id);
        return "redirect:/resources";
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadResource(@PathVariable Long id, @AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.badRequest().build();
        }
        String email = principal.getAttribute("email");
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Resource resource = resourceService.findById(id).orElseThrow(() -> new RuntimeException("Resource not found"));
        
        if (!resource.getOwner().getId().equals(user.getId())) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            Path filePath = fileStorageLocation.resolve(resource.getFilePath());
            byte[] data = Files.readAllBytes(filePath);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + resource.getFilePath() + "\"")
                    .body(data);
        } catch (IOException e) {
            throw new RuntimeException("Failed to download file", e);
        }
    }
}