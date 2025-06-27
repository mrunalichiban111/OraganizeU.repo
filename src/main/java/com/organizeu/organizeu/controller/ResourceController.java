package com.organizeu.organizeu.controller;

import com.organizeu.organizeu.model.Resource;
import com.organizeu.organizeu.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    @GetMapping
    public String listResources(@RequestParam(value = "section", required = false) String sectionName,
                                Model model,
                                @SessionAttribute(value = "user", required = false) com.organizeu.organizeu.model.User user) {
        if (user == null) {
            // fallback guest
            user = new com.organizeu.organizeu.model.User();
            user.setName("Guest");
            user.setEmail("guest@organizeu.com");
        }
        List<com.organizeu.organizeu.model.Section> sections = resourceService.getAllSections(user);
        model.addAttribute("sections", sections);
        String currentSection = sectionName != null ? sectionName : (sections.isEmpty() ? null : sections.get(0).getName());
        model.addAttribute("currentSection", currentSection);
        List<com.organizeu.organizeu.model.FileResource> currentFiles = new java.util.ArrayList<>();
        List<com.organizeu.organizeu.model.LinkResource> currentLinks = new java.util.ArrayList<>();
        if (currentSection != null) {
            com.organizeu.organizeu.model.Section sec = resourceService.getSectionByName(currentSection, user);
            if (sec.getFiles() != null) currentFiles.addAll(sec.getFiles());
            if (sec.getLinks() != null) currentLinks.addAll(sec.getLinks());
        }
        model.addAttribute("currentFiles", currentFiles);
        model.addAttribute("currentLinks", currentLinks);
        return "resource_management";
    }

    @PostMapping("/add-section")
    public String addSection(@RequestParam String sectionName,
                             @SessionAttribute(value = "user", required = false) com.organizeu.organizeu.model.User user,
                             Model model) {
        if (user == null) {
            user = new com.organizeu.organizeu.model.User();
            user.setName("Guest");
            user.setEmail("guest@organizeu.com");
        }
        try {
            resourceService.addSection(sectionName, user);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/resources?section=" + sectionName;
    }

    @PostMapping("/add-link")
    public String addLink(@RequestParam String linkTitle,
                         @RequestParam String linkUrl,
                         @RequestParam String sectionName,
                         @SessionAttribute(value = "user", required = false) com.organizeu.organizeu.model.User user,
                         Model model) {
        if (user == null) {
            user = new com.organizeu.organizeu.model.User();
            user.setName("Guest");
            user.setEmail("guest@organizeu.com");
        }
        try {
            resourceService.addLink(linkTitle, linkUrl, sectionName, user);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/resources?section=" + sectionName;
    }

    @PostMapping("/upload-file")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                            @RequestParam("title") String title,
                            @RequestParam("sectionName") String sectionName,
                            @SessionAttribute(value = "user", required = false) com.organizeu.organizeu.model.User user,
                            Model model) {
        if (user == null) {
            user = new com.organizeu.organizeu.model.User();
            user.setName("Guest");
            user.setEmail("guest@organizeu.com");
        }
        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path targetLocation = fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation);
            resourceService.addFile(title, fileName, fileName, sectionName, user);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/resources?section=" + sectionName;
    }

    @PostMapping("/delete-link")
    public String deleteLink(@RequestParam Long linkId,
                             @RequestParam String sectionName,
                             @SessionAttribute(value = "user", required = false) com.organizeu.organizeu.model.User user,
                             Model model) {
        if (user == null) {
            user = new com.organizeu.organizeu.model.User();
            user.setName("Guest");
            user.setEmail("guest@organizeu.com");
        }
        try {
            resourceService.deleteLink(linkId, user);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/resources?section=" + sectionName;
    }

    @PostMapping("/delete-file")
    public String deleteFile(@RequestParam Long fileId,
                             @RequestParam String sectionName,
                             @SessionAttribute(value = "user", required = false) com.organizeu.organizeu.model.User user,
                             Model model) {
        if (user == null) {
            user = new com.organizeu.organizeu.model.User();
            user.setName("Guest");
            user.setEmail("guest@organizeu.com");
        }
        try {
            resourceService.deleteFile(fileId, user);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/resources?section=" + sectionName;
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
                               @RequestParam("category") String category) {
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
            // No owner set

            resourceService.saveResource(resource);
            return "redirect:/resources";
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @GetMapping("/{id}")
    public String viewResource(@PathVariable Long id, Model model) {
        Resource resource = resourceService.findById(id).orElseThrow(() -> new RuntimeException("Resource not found"));
        model.addAttribute("resource", resource);
        return "resource_management";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Resource resource = resourceService.findById(id).orElseThrow(() -> new RuntimeException("Resource not found"));
        model.addAttribute("resource", resource);
        return "resource_management";
    }

    @PostMapping("/{id}")
    public String updateResource(@PathVariable Long id,
                               @RequestParam("file") MultipartFile file,
                               @RequestParam("title") String title,
                               @RequestParam("description") String description,
                               @RequestParam("category") String category) {
        try {
            Resource resource = resourceService.findById(id).orElseThrow(() -> new RuntimeException("Resource not found"));
            
            if (!file.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path targetLocation = fileStorageLocation.resolve(fileName);
                Files.copy(file.getInputStream(), targetLocation);
                resource.setFilePath(fileName);
            }

            resource.setTitle(title);
            resource.setDescription(description);
            resource.setCategory(category);

            resourceService.saveResource(resource);
            return "redirect:/resources";
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return "redirect:/resources";
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadResource(@PathVariable Long id) {
        Resource resource = resourceService.findById(id).orElseThrow(() -> new RuntimeException("Resource not found"));
        
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