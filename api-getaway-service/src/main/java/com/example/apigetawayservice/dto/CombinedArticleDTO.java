package com.example.apigetawayservice.dto;

import java.time.LocalDateTime;

public class CombinedArticleDTO {
    private long id;
    private String name;
    private String author;
    private LocalDateTime createdAt;
    private long size;
    private String status;
    private String content;


    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public long getSize() {
        return size;
    }

    public String getStatus() {
        return status;
    }

    public String getContent() {
        return content;
    }
}
