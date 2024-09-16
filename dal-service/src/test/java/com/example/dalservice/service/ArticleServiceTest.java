package com.example.dalservice.service;

import com.example.dalservice.Service.ArticleService;
import com.example.dalservice.entity.Article;
import com.example.dalservice.repository.JPA.ArticleJpaRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ArticleServiceTest {

    private ArticleJpaRepository articleRepository = Mockito.mock(ArticleJpaRepository.class);
    private ArticleService articleService = new ArticleService(articleRepository);

    @Test
    void testGetArticleById() {
        // Arrange: Mock the repository response
        Article article = new Article();
        article.setId(1L);
        article.setName("Test Article");

        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));

        // Act: Call the service method
        Optional<Article> foundArticle = articleService.getArticleById(1L);

        // Assert: Check that the service returned the correct result
        assertTrue(foundArticle.isPresent());
        assertEquals("Test Article", foundArticle.get().getName());
    }

    @Test
    void testSaveArticle() {
        try {
            // Arrange: Mock the repository save method
            Article article = new Article();
            article.setName("New Article");

            when(articleRepository.save(article)).thenReturn(article);

            // Act: Call the service method
            Article savedArticle = articleService.saveArticle(article);

            // Assert: Check that the service returned the saved article
            assertEquals("New Article", savedArticle.getName());
        }catch(Exception e){
            // Handle the exception if anything goes wrong
            fail("Exception occurred while saving the article: " + e.getMessage());
        }
    }
}
