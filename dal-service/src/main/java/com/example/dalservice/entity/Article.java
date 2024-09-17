package com.example.dalservice.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "articles")// maps this class to the 'articles' table in the database
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //automatic increment the id
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "created_at", nullable = false,columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    private long size;

    private String author;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)  // Maps to the 'status' column (enum 'pending', 'indexed')
    private ArticleStatus status;

    public Article() {
        this.createdAt = LocalDateTime.now(); // Set default value to the current timestamp
        this.status = ArticleStatus.pending;
    }

    // Parameterized constructor
    public Article(String name, String author, long size, ArticleStatus status) {
        this.name = name;
        this.author = author;
        this.size = size;
        this.status = status;
        this.createdAt = LocalDateTime.now(); // Set default timestamp
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setSize(long size) {
        this.size = size;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public void setStatus(ArticleStatus status) {
        this.status = status;
    }
    public long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public long getSize() {
        return size;
    }
    public String getAuthor() {
        return author;
    }
    public ArticleStatus getStatus() {
        return status;
    }

}

