package com.example.dalservice.service;

import com.example.dalservice.Service.ArticleService;
import com.example.dalservice.Service.WordsMappingService;
import com.example.dalservice.entity.WordsMapping;
import com.example.dalservice.repository.JPA.ArticleJpaRepository;
import com.example.dalservice.repository.JPA.WordsMappingJpaRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class WordsMappingServiceTest {

    private WordsMappingJpaRepository wordsMappingRepository = Mockito.mock(WordsMappingJpaRepository.class);
    private ArticleService articleService= Mockito.mock(ArticleService.class);
    private WordsMappingService wordsMappingService = new WordsMappingService(wordsMappingRepository,articleService);

    @Test
    void testSaveWordsMapping() {
        // Arrange: Create a WordsMapping object and mock the save method
        WordsMapping wordsMapping = new WordsMapping();
        wordsMapping.setWord("testword");
        wordsMapping.setArticleId(1L);
        wordsMapping.setOffsets("[0, 5, 10]");

        when(wordsMappingRepository.save(wordsMapping)).thenReturn(wordsMapping);

        // Act: Call the service method
        WordsMapping savedMapping = wordsMappingService.saveWordsMapping(wordsMapping);

        // Assert: Check that the service returned the saved mapping
        assertEquals("testword", savedMapping.getWord());
        assertEquals(1L, savedMapping.getArticleId());
        assertEquals("[0, 5, 10]", savedMapping.getOffsets());
    }

    @Test
    void testFindWordsByWord() {
        // Arrange: Mock the repository response for findByWord method
        WordsMapping wordsMapping1 = new WordsMapping();
        wordsMapping1.setWord("testword");
        wordsMapping1.setArticleId(1L);
        wordsMapping1.setOffsets("[0, 5]");

        WordsMapping wordsMapping2 = new WordsMapping();
        wordsMapping2.setWord("testword");
        wordsMapping2.setArticleId(2L);
        wordsMapping2.setOffsets("[10, 15]");

        List<WordsMapping> mockResponse = Arrays.asList(wordsMapping1, wordsMapping2);

        when(wordsMappingRepository.findByWord("testword")).thenReturn(mockResponse);

        // Act: Call the service method
        List<WordsMapping> result = wordsMappingService.findWordsByWord("testword");

        // Assert: Check that the service returned the correct result
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getArticleId());
        assertEquals(2L, result.get(1).getArticleId());
        assertEquals("[0, 5]", result.get(0).getOffsets());
        assertEquals("[10, 15]", result.get(1).getOffsets());
    }

    @Test
    void testGetAllWordsMappings() {
        // Arrange: Mock the repository response for findAll method
        WordsMapping wordsMapping1 = new WordsMapping();
        wordsMapping1.setWord("testword1");
        wordsMapping1.setArticleId(1L);
        wordsMapping1.setOffsets("[0, 5]");

        WordsMapping wordsMapping2 = new WordsMapping();
        wordsMapping2.setWord("testword2");
        wordsMapping2.setArticleId(2L);
        wordsMapping2.setOffsets("[10, 15]");

        List<WordsMapping> mockResponse = Arrays.asList(wordsMapping1, wordsMapping2);

        when(wordsMappingRepository.findAll()).thenReturn(mockResponse);

        // Act: Call the service method
        List<WordsMapping> result = wordsMappingService.getAllWordsMappings();

        // Assert: Check that the service returned the correct result
        assertEquals(2, result.size());
        assertEquals("testword1", result.get(0).getWord());
        assertEquals("testword2", result.get(1).getWord());
        assertEquals(1L, result.get(0).getArticleId());
        assertEquals(2L, result.get(1).getArticleId());
    }
}
