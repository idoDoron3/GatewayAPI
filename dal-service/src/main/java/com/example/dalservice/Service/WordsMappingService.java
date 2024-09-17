package com.example.dalservice.Service;

import com.example.dalservice.entity.Article;
import com.example.dalservice.entity.ArticleStatus;
import com.example.dalservice.entity.WordsMapping;
import com.example.dalservice.repository.JPA.WordsMappingJpaRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class WordsMappingService {
    private final WordsMappingJpaRepository wordsMappingRepository;
    private final ArticleService articleService;

    // Constructor-based dependency injection
    public WordsMappingService(WordsMappingJpaRepository wordsMappingRepository,ArticleService articleService){
        this.wordsMappingRepository = wordsMappingRepository;
        this.articleService =articleService;
    }
    // Save a single WordsMapping entity
    public WordsMapping saveWordsMapping(WordsMapping wordsMapping) {
        return wordsMappingRepository.save(wordsMapping);
    }
    // Find all word mappings by a specific word
    public List<WordsMapping> findWordsByWord(String word) {
//        List<WordsMapping> result =  this.wordsMappingRepository.findByWord(word);
//        if (result.isEmpty()) {
//            throw new NoSuchElementException("No results found for the word: " + word);
//        }
//
//        return result;
        return wordsMappingRepository.findByWord(word);

    }
    public List<WordsMapping> getAllWordsMappings() {
        return this.wordsMappingRepository.findAll();
    }
    // Save a list of word mappings and update the corresponding article's status
    public List<WordsMapping> saveAllWordsMappings(List<WordsMapping> wordMappings) {
        // Extract the article ID from the first word mapping
        Long articleId = wordMappings.get(0).getArticleId();

        // Check if the article exists
        Optional<Article> articleOpt = articleService.getArticleById(articleId);
        if (!articleOpt.isPresent()) {
            throw new NoSuchElementException("Article with ID " + articleId + " does not exist");
        }

        // Save word mappings
        List<WordsMapping> savedWordMappings = wordsMappingRepository.saveAll(wordMappings);
        // Update the article status to "indexed"
        articleService.updateArticleStatus(articleId, ArticleStatus.indexed);

        return savedWordMappings;
    }
}


