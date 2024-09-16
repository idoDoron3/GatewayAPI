package com.example.dalservice.repository.jpa;

import com.example.dalservice.entity.Article;
import com.example.dalservice.entity.ArticleContent;
import com.example.dalservice.entity.ArticleStatus;
import com.example.dalservice.repository.JPA.ArticleContentJpaRepository;
import com.example.dalservice.repository.JPA.ArticleJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ArticleContentRepositoryTest {

    @Autowired
    private ArticleJpaRepository articleRepository;

    @Autowired
    private ArticleContentJpaRepository articleContentRepository;

    @Test
    void testSaveAndFindArticleContent() {
        // First, save an article
        Article article = new Article();
        article.setName("Test Article for Content");
        article.setStatus(ArticleStatus.pending);
        article.setSize(200);
        article.setAuthor("Author A");
        Article savedArticle = articleRepository.save(article);

        // Now save the associated article content
        ArticleContent content = new ArticleContent(savedArticle,"Test Content".getBytes() );
//        content.setArticle(savedArticle);
//        content.setCompressedContent("Test Content".getBytes());
        ArticleContent savedContent = articleContentRepository.save(content);

        Optional<ArticleContent> foundContent = articleContentRepository.findById(savedContent.getArticleId());
        assertTrue(foundContent.isPresent());
        assertArrayEquals("Test Content".getBytes(), foundContent.get().getCompressedContent());
    }

    @Test
    void testUpdateArticleContent() {
        // First, save an article
        Article article = new Article();
        article.setName("Test Article for Content");
        article.setStatus(ArticleStatus.pending);
        article.setSize(200);
        article.setAuthor("Author A");
        Article savedArticle = articleRepository.save(article);

        // Now save the associated article content
        ArticleContent content = new ArticleContent();
        content.setArticle(savedArticle);
        content.setCompressedContent("Initial Content".getBytes());
        ArticleContent savedContent = articleContentRepository.save(content);

        // Update the content
        savedContent.setCompressedContent("Updated Content".getBytes());
        ArticleContent updatedContent = articleContentRepository.save(savedContent);

        Optional<ArticleContent> foundContent = articleContentRepository.findById(updatedContent.getArticleId());
        assertTrue(foundContent.isPresent());
        assertArrayEquals("Updated Content".getBytes(), foundContent.get().getCompressedContent());
    }

    @Test
    void testDeleteArticleContent() {
        // First, save an article
        Article article = new Article();
        article.setName("Test Article for Content");
        article.setStatus(ArticleStatus.pending);
        article.setSize(200);
        article.setAuthor("Author A");
        Article savedArticle = articleRepository.save(article);

        // Now save the associated article content
        ArticleContent content = new ArticleContent();
        content.setArticle(savedArticle);
        content.setCompressedContent("Test Content".getBytes());
        ArticleContent savedContent = articleContentRepository.save(content);

        // Delete the content
        articleContentRepository.delete(savedContent);

        Optional<ArticleContent> foundContent = articleContentRepository.findById(savedContent.getArticleId());
        assertFalse(foundContent.isPresent());  // Ensure the content was deleted
    }
}
