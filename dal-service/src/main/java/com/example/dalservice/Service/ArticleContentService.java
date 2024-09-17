package com.example.dalservice.Service;

import com.example.dalservice.entity.ArticleContent;
import com.example.dalservice.repository.JPA.ArticleContentJpaRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ArticleContentService {

    private final ArticleContentJpaRepository articleContentRepository;
    // Constructor-based dependency injection
    public ArticleContentService(ArticleContentJpaRepository articleContentRepository_){
        this.articleContentRepository = articleContentRepository_;
    }

    // Save or article content
    public ArticleContent saveArticleContent(ArticleContent articleContent){
        return this.articleContentRepository.save(articleContent);
    }

    // Retrieve article content by ID
    public Optional<ArticleContent> getArticleContentById(Long id){//TODO - check what happen if there is no such id
        return this.articleContentRepository.findById(id);

    }
    // Retrieve all article content
    public List<ArticleContent> getAllArticleContents() {
        return articleContentRepository.findAll();
    }
    // Delete article content by ID
    public void deleteArticleContentById(Long id) {
        articleContentRepository.deleteById(id);
    }


}
