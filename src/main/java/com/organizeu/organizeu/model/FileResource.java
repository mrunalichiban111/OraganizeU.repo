package com.organizeu.organizeu.model;

import java.util.UUID;

public class FileResource {

    private String id;
    private String title;
    private String name;
    private String url;

    public FileResource(String title,String name ,String url) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.name = name;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
