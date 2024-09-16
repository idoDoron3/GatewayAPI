// File: ArticleContentDTO.java
package com.example.parserservice.DTO;

public class ArticleContentDTO {
    private Long articleId;
    private String content;

    // Getters and Setters
    public Long getArticleId() {
        return articleId;
    }
    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }
    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "ArticleContentDTO{" +
                "articleId=" + articleId +
                ", content='" + content + '\'' +
                '}';
    }
}
