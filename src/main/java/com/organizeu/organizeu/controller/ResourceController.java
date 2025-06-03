package com.organizeu.organizeu.controller;

import com.organizeu.organizeu.model.Section;
import com.organizeu.organizeu.service.ResourceService;
import com.organizeu.organizeu.service.SectionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ContentDisposition;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;


import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;

//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.io.IOException;

import java.util.List;

@Controller
public class ResourceController {

    private final ResourceService resourceService;
    private final SectionService sectionService;

    @Autowired
    public ResourceController(ResourceService resourceService, SectionService sectionService) {
        this.resourceService = resourceService;
        this.sectionService = sectionService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("sections", resourceService.getSections());
        model.addAttribute("selectedSection", resourceService.getSelectedSection());
        model.addAttribute("hasSections", resourceService.hasSections());
        return "resource_management";
    }

    @PostMapping("/add-section")
    public String addSection(@RequestParam String sectionName) {
        resourceService.addSection(sectionName);
        return "redirect:/resources?section=" + sectionName;
    }

    @GetMapping("/select-section")
    public String selectSection(@RequestParam String name) {
        resourceService.selectSection(name);
        return "redirect:/resources?section=" + name;
    }

    @GetMapping("/resources")
    public String showResources(@RequestParam(name = "section", required = false) String sectionName, Model model) {
        if (sectionName == null) {
            sectionName = "default";
        }

        List<Section> sections = sectionService.getAllSections();
        Section currentSection = sectionService.getSectionByName(sectionName);

        model.addAttribute("sections", sections);
        model.addAttribute("currentSection", currentSection != null ? currentSection.getName() : null);
        model.addAttribute("currentLinks", currentSection != null ? currentSection.getLinks() : null);
        model.addAttribute("currentFiles", currentSection != null ? currentSection.getFiles() : null);

        return "resource_management";
    }


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





    @PostMapping("/add-file")
    public String addFile(@RequestParam String sectionName,
                          @RequestParam String fileName,
                          @RequestParam String fileUrl) {
        Section section = sectionService.getSectionByName(sectionName);
        if (section != null && fileName != null && fileUrl != null) {
            section.addFile(fileName.trim(),fileName.trim(), fileUrl.trim());
        }
        return "redirect:/resources?section=" + sectionName;
    }

    @PostMapping("/upload-file")
    public String uploadFile(@RequestParam("sectionName") String sectionName,
                             @RequestParam("title") String title,
                             @RequestParam("file") MultipartFile file) throws IOException {

        if (!file.isEmpty()) {
            String uploadDir = System.getProperty("java.io.tmpdir");
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);


            String fileUrl = "/files/" + fileName;


            Section section = sectionService.getSectionByName(sectionName);
            if (section != null){
                section.addFile(title, fileName, fileUrl);
            }
        }

        return "redirect:/resources?section=" + sectionName;
    }

    @GetMapping("/view-file/{fileName}")
    public ResponseEntity<byte[]> viewFile(@PathVariable String fileName) throws IOException {
        String uploadDir = System.getProperty("java.io.tmpdir");
        Path filePath = Paths.get(uploadDir, fileName);


        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }


        String contentType = Files.probeContentType(filePath);


        if (contentType == null) {
            contentType = "application/octet-stream";
        }


        byte[] fileBytes = Files.readAllBytes(filePath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
//        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentDisposition(ContentDisposition.inline().filename(fileName).build());


        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }

    @PostMapping("/delete-file")
    public String deleteFile(@RequestParam String fileId,
                             @RequestParam String sectionName) {
        Section section = sectionService.getSectionByName(sectionName);
        if (section != null) {
            section.removeFileById(fileId);
        }
        return "redirect:/resources?section=" + sectionName;
    }

    @PostMapping("/delete-link")
    public String deleteLink(@RequestParam String linkId,
                             @RequestParam String sectionName) {
        Section section = sectionService.getSectionByName(sectionName);
        if (section != null) {
            section.removeLinkById(linkId);
        }
        return "redirect:/resources?section=" + sectionName;
    }

}
