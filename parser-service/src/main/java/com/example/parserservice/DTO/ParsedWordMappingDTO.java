package com.example.parserservice.DTO;

public class ParsedWordMappingDTO {
    private String word;
    private Long articleId;
    private String offsets;
    public ParsedWordMappingDTO(String word_, Long articleId_, String lst){
        this.word = word_;
        this.articleId = articleId_;
        this.offsets = lst;
    }

    public String getWord() {
        return word;
    }

    public String getOffsets() {
        return offsets;
    }

    public Long getArticleId() {
        return this.articleId;
    }
}


