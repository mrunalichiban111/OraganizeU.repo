package com.organizeu.organizeu.model;

import java.util.ArrayList;
import java.util.List;

public class Section {

    private String name;
    private List<LinkResource> links = new ArrayList<>();
    private List<FileResource> files = new ArrayList<>();

    public Section(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<LinkResource> getLinks() {
        return links;
    }

    public List<FileResource> getFiles() {
        return files;
    }

//    public void addLink(LinkResource link) {
//        links.add(link);
//    }
//    public void addFile(FileResource file) {
//        files.add(file);
//    }

    public void addLink(String title, String url) {
        this.links.add(new LinkResource(title, url));
    }

    public void addFile(String title, String name ,String url) {
        this.files.add(new FileResource(title,name, url));
    }

    public void removeLinkById(String id) {
        links.removeIf(link -> link.getId().equals(id));
    }

    public void removeFileById(String id) {
        files.removeIf(file -> file.getId().equals(id));
    }
}
