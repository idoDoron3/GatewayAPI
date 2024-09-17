package com.example.dalservice.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dalservice.Service.ArticleContentService;
import com.example.dalservice.Service.WordsMappingService;


import com.example.dalservice.entity.Article;
import com.example.dalservice.entity.ArticleStatus;
import com.example.dalservice.repository.JPA.ArticleJpaRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.slf4j.Logger;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ArticleService {
    // Logger for logging operations in the service
    private static final Logger logger = LoggerFactory.getLogger(ArticleService.class);

    private final ArticleJpaRepository articleRepository;
    // Constructor-based dependency injection
    @Autowired
    public ArticleService(ArticleJpaRepository articleRepository_){
        this.articleRepository = articleRepository_;
    }

    // Save a new article and throw an exception if an article with the same name and author already exists
    public Article saveArticle(Article article) throws Exception {
        // Check if an article with the same name and author exists
        Optional<Article> existingArticle = articleRepository.findByNameAndAuthor(article.getName(), article.getAuthor());
        if (existingArticle.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An article with the same name and author already exists.");
        }
        // Ensure the status is converted to lowercase before saving
        article.setStatus(ArticleStatus.valueOf(article.getStatus().toString().toLowerCase()));

        logger.info("Saving article with name:" + article.getName());
        return this.articleRepository.save(article);

    }
    // retrieve an article by its ID, throws NoSuchElementException if not found
    public Optional<Article> getArticleById(Long id) {
        return articleRepository.findById(id);

    }

    // Method to update the article's status
    public void updateArticleStatus(Long articleId, ArticleStatus newStatus) {
        Optional<Article> articleOpt = articleRepository.findById(articleId);
        if (articleOpt.isPresent()) {
            Article article = articleOpt.get();
            article.setStatus(newStatus);
            articleRepository.save(article);  // Save the article with updated status
        } else {
            throw new NoSuchElementException("Article with ID " + articleId + " not found");
        }
    }

    // Delete an article by its ID - and also all of this article appearances in db - cascade
    public void deleteArticle(Long id){
        this.articleRepository.deleteById(id);
    }
}

