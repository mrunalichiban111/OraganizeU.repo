package com.organizeu.organizeu.controller;

import com.organizeu.organizeu.model.Event;
import com.organizeu.organizeu.model.Section;
import com.organizeu.organizeu.service.EventService;
import com.organizeu.organizeu.service.ResourceService;
import com.organizeu.organizeu.service.SectionService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
public class PageController {

    private final ResourceService resourceService;
    private final SectionService sectionService;
    private final EventService eventService;

    public PageController(ResourceService resourceService,
                          SectionService sectionService,
                          EventService eventService) {
        this.resourceService = resourceService;
        this.sectionService = sectionService;
        this.eventService = eventService;
    }

    // ==================== Calendar Endpoints ====================
    @GetMapping("/about")
    public String showAboutUs() {
        return "aboutus"; // This should match the name of your Thymeleaf template file (without the .html extension)
    }
    @GetMapping("/schedule")
    public String showSchedule() {
        return "schedule";
    }

    @GetMapping("/api/events")
    @ResponseBody
    public List<Event> getEvents(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end
    ) {
        return eventService.getEventsBetween(start, end);
    }

    @PostMapping("/api/events")
    @ResponseBody
    public Event createEvent(@RequestBody Event event) {
        return eventService.createEvent(event);
    }

    @PutMapping("/api/events/{id}")
    @ResponseBody
    public ResponseEntity<Event> updateEvent(
            @PathVariable Long id,
            @RequestBody Event event
    ) {
        return eventService.updateEvent(id, event)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/api/events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        return eventService.deleteEvent(id)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    // ==================== Existing Resource Endpoints ====================

    // Home page
    @GetMapping("/")
    public String indexPage() {
        return "index";
    }

    // Login page
    @GetMapping("/login2")
    public String loginPage() {
        return "login2";
    }

    // Show resources
    @GetMapping("/resources")
    public String showResources(
            @RequestParam(name = "section", required = false) String sectionName,
            Model model
    ) {
        if (sectionName == null) {
            Section selected = resourceService.getSelectedSection();
            sectionName = (selected != null ? selected.getName() : null);
        }

        List<Section> sections = sectionService.getAllSections();
        Section currentSection = sectionService.getSectionByName(sectionName);

        model.addAttribute("sections", sections);
        model.addAttribute("currentSection",
                currentSection != null ? currentSection.getName() : null);
        model.addAttribute("currentLinks",
                currentSection != null ? currentSection.getLinks() : null);
        model.addAttribute("currentFiles",
                currentSection != null ? currentSection.getFiles() : null);

        return "resource_management";
    }

    // Add a section
    @PostMapping("/add-section")
    public String addSection(@RequestParam String sectionName) {
        resourceService.addSection(sectionName);
        return "redirect:/resources?section=" + sectionName;
    }

    // Select a section
    @GetMapping("/select-section")
    public String selectSection(@RequestParam String name) {
        resourceService.selectSection(name);
        return "redirect:/resources?section=" + name;
    }

    // Add a link
    @PostMapping("/add-link")
    public String addLink(@RequestParam String sectionName,
                          @RequestParam String linkTitle,
                          @RequestParam String linkUrl) {
        Section section = sectionService.getSectionByName(sectionName);
        if (section != null && linkTitle != null && linkUrl != null) {
            section.addLink(linkTitle.trim(), linkUrl.trim());
        }
        return "redirect:/resources?section=" + sectionName;
    }

    // Delete a link
    @PostMapping("/delete-link")
    public String deleteLink(@RequestParam String linkId,
                             @RequestParam String sectionName) {
        Section section = sectionService.getSectionByName(sectionName);
        if (section != null) {
            section.removeLinkById(linkId);
        }
        return "redirect:/resources?section=" + sectionName;
    }


    // Upload a file
    @PostMapping("/upload-file")
    public String uploadFile(
            @RequestParam("sectionName") String sectionName,
            @RequestParam("title") String title,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        if (!file.isEmpty()) {
            String uploadDir = System.getProperty("java.io.tmpdir");
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.copy(file.getInputStream(), filePath,
                    StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/files/" + fileName;
            Section section = sectionService.getSectionByName(sectionName);
            if (section != null) {
                section.addFile(title, fileName, fileUrl);
            }
        }
        return "redirect:/resources?section=" + sectionName;
    }

    // View a file inline
    @GetMapping("/view-file/{fileName}")
    public ResponseEntity<byte[]> viewFile(@PathVariable String fileName)
            throws IOException {
        String uploadDir = System.getProperty("java.io.tmpdir");
        Path filePath = Paths.get(uploadDir, fileName);
        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        byte[] data = Files.readAllBytes(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDisposition(
                ContentDisposition.inline().filename(fileName).build()
        );
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    // Delete a file by ID
    @PostMapping("/delete-file")
    public String deleteFile(@RequestParam String fileId,
                             @RequestParam String sectionName) {
        Section section = sectionService.getSectionByName(sectionName);
        if (section != null) {
            section.removeFileById(fileId);
        }
        return "redirect:/resources?section=" + sectionName;
    }
}
