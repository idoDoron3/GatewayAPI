package com.example.dalservice.service;

import com.example.dalservice.Service.ArticleContentService;
import com.example.dalservice.entity.ArticleContent;
import com.example.dalservice.repository.JPA.ArticleContentJpaRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ArticleContentServiceTest {

    private ArticleContentJpaRepository articleContentRepository = Mockito.mock(ArticleContentJpaRepository.class);
    private ArticleContentService articleContentService = new ArticleContentService(articleContentRepository);

    @Test
    void testGetArticleContentById() {
        // Arrange: Mock the repository response
        ArticleContent articleContent = new ArticleContent();
        articleContent.setArticleId(1L);
        articleContent.setCompressedContent(new byte[]{1, 2, 3});

        when(articleContentRepository.findById(1L)).thenReturn(Optional.of(articleContent));

        // Act: Call the service method
        Optional<ArticleContent> foundContent = articleContentService.getArticleContentById(1L);

        // Assert: Check that the service returned the correct result
        assertTrue(foundContent.isPresent());
        assertArrayEquals(new byte[]{1, 2, 3}, foundContent.get().getCompressedContent());
    }

    @Test
    void testSaveArticleContent() {
        // Arrange: Mock the repository save method
        ArticleContent articleContent = new ArticleContent();
        articleContent.setCompressedContent(new byte[]{1, 2, 3});

        when(articleContentRepository.save(articleContent)).thenReturn(articleContent);

        // Act: Call the service method
        ArticleContent savedContent = articleContentService.saveArticleContent(articleContent);

        // Assert: Check that the service returned the saved content
        assertArrayEquals(new byte[]{1, 2, 3}, savedContent.getCompressedContent());
    }
}
