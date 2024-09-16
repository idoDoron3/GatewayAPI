package com.example.apigetawayservice.dto;

public class WordsMapping {
    private String word;
    private Long articleId;
    private String offsets;

    // Getters and Setters
    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public String getOffsets() {
        return offsets;
    }

    public void setOffsets(String offsets) {
        this.offsets = offsets;
    }
}

