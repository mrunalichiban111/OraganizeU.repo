package com.organizeu.organizeu.model;

import java.util.UUID;

public class LinkResource {

    private String id;
    private String title;
    private String url;

    public LinkResource(String title, String url) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }
}
