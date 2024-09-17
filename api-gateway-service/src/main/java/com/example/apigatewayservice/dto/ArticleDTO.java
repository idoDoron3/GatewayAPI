package com.example.apigatewayservice.dto;

import java.time.LocalDateTime;

public class ArticleDTO {
    private Long id;
    private String name;
    private String author;
    private LocalDateTime createdAt;

    private long size;
    private String status;
    private byte[] content;

    public ArticleDTO(String name, String author, long size, String status, byte[] content) {
        this.name = name;
        this.author = author;
        this.size = size;
        this.status = status;
        this.content = content;
    }

    // Getters and Setters

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public long getSize() {
        return size;
    }

    public String getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public byte[] getContent() {
        return content;
    }
}
