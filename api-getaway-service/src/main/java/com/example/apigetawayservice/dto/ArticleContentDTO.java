package com.example.apigetawayservice.dto;

public class ArticleContentDTO {
    private Long articleId;
    private String content;
    public ArticleContentDTO(Long articleId, String content) {
        this.articleId = articleId;
        this.content = content;
    }

    public Long getArticleId() {
        return articleId;
    }

    public String getContent() {
        return content;
    }

    // Getters and Setters

}
