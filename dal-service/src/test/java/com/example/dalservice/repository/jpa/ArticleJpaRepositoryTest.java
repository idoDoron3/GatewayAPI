package com.example.dalservice.repository.jpa;

import com.example.dalservice.entity.Article;
import com.example.dalservice.entity.ArticleStatus;
import com.example.dalservice.repository.JPA.ArticleJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test") // This will ensure it picks up application-test.properties
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Use NONE for real DB or Replace.ANY for embedded
public class ArticleJpaRepositoryTest {

    @Autowired
    private ArticleJpaRepository articleRepository;

    @Test
    void testSaveAndFindArticle() {
        Article article = new Article();
        article.setName("Integration Test Article");
        article.setStatus(ArticleStatus.pending);  // Set a default status before saving


        Article savedArticle = articleRepository.save(article);

        Optional<Article> foundArticle = articleRepository.findById(savedArticle.getId());
        assertTrue(foundArticle.isPresent());
        assertEquals("Integration Test Article", foundArticle.get().getName());
    }
    @Test
    void testDeleteArticle() {
        Article article = new Article();
        article.setName("To be Deleted");
        article.setStatus(ArticleStatus.pending);

        Article savedArticle = articleRepository.save(article);
        articleRepository.delete(savedArticle);

        Optional<Article> foundArticle = articleRepository.findById(savedArticle.getId());
        assertFalse(foundArticle.isPresent());  // Ensure the article was deleted
    }

    @Test
    void testUpdateArticle() {
        Article article = new Article();
        article.setName("Original Name");
        article.setStatus(ArticleStatus.pending);

        Article savedArticle = articleRepository.save(article);

        // Update the article
        savedArticle.setName("Updated Name");
        Article updatedArticle = articleRepository.save(savedArticle);

        Optional<Article> foundArticle = articleRepository.findById(updatedArticle.getId());
        assertTrue(foundArticle.isPresent());
        assertEquals("Updated Name", foundArticle.get().getName());
    }
}
