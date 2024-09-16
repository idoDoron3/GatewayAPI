package com.example.dalservice.entity;

import java.io.Serializable;
import java.util.Objects;

/* jpa required implement serialize primary key because it composed of 2 keys and need to be compared and hash*/
public class WordsMappingKey implements Serializable {

    private String word;
    private Long articleId;

    public WordsMappingKey(){}

    public WordsMappingKey (String word_, Long articleId_ ){
        this.word = word_;
        this.articleId = articleId_;
    }

    //implement the equal method
    @Override
    public boolean equals(Object object){
        //reference memory location
        if(this == object){
            return true;
        }
        if(object == null || getClass() != object.getClass()){
            return false;
        }
        WordsMappingKey current = (WordsMappingKey) object;
        return Objects.equals(current.word, this.word) && Objects.equals(this.articleId, current.articleId);
    }

    //implement the hash method
    @Override
    public int hashCode(){
        return Objects.hash(this.word,this.articleId);
    }

    //getters and setters
    public String getWord() {
        return word;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setWord(String word_){
        this.word = word_;
    }

    public void setArticleId(Long articleId1){
        this.articleId = articleId1;
    }

}