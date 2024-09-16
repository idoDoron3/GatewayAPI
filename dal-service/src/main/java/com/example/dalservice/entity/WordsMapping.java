package com.example.dalservice.entity;
import jakarta.persistence.*;


@Entity
@Table(name = "words_mapping")
@IdClass(WordsMappingKey.class)  // Specifies that this entity has a composite key
public class WordsMapping {
    @Id
    private String word;
    @Id
    @Column(name = "article_id")  // Ensures this is mapped correctly
    private Long articleId;

    @ManyToOne
    @JoinColumn(name = "article_id", insertable = false, updatable = false)
    private Article article;

    @Column(columnDefinition = "JSON") //store the offsets in json format
    private String offsets;

    // Default constructor (required by JPA)
    public WordsMapping() {
    }

    // Constructor with word and articleId (required for composite key tests)
    public WordsMapping(String word, Long articleId) {
        this.word = word;
        this.articleId = articleId;
    }

    public String getWord(){
        return this.word;
    }

    public String getOffsets(){
        return this.offsets;
    }

    public Long getArticleId(){
        return this.articleId;
    }

    public void setWord(String word1){
        this.word = word1;
    }
    public void setArticleId(Long articleId1){
        this.articleId = articleId1;
    }
    public void setOffsets(String offsets1){
        this.offsets = offsets1;
    }
}
