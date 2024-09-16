package com.example.dalservice.repository.jpa;

import com.example.dalservice.entity.Article;
import com.example.dalservice.entity.ArticleStatus;
import com.example.dalservice.entity.WordsMapping;
import com.example.dalservice.entity.WordsMappingKey;
import com.example.dalservice.repository.JPA.ArticleJpaRepository;
import com.example.dalservice.repository.JPA.WordsMappingJpaRepository;
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
public class WordsMappingRepositoryTest {

    @Autowired
    private ArticleJpaRepository articleRepository;

    @Autowired
    private WordsMappingJpaRepository wordsMappingRepository;

    @Test
    void testSaveAndFindWordsMapping() {
        // First, save an article
        Article article = new Article();
        article.setName("Test Article for Words");
        article.setStatus(ArticleStatus.pending);
        article.setSize(100);
        article.setAuthor("Author B");
        Article savedArticle = articleRepository.save(article);

        // Now save a words mapping
        WordsMapping wordsMapping = new WordsMapping();
        wordsMapping.setWord("testword");
        wordsMapping.setArticleId(savedArticle.getId());
        wordsMapping.setOffsets("[0, 10, 20]");
        WordsMapping savedMapping = wordsMappingRepository.save(wordsMapping);

        Optional<WordsMapping> foundMapping = wordsMappingRepository.findById(new WordsMappingKey(wordsMapping.getWord(), wordsMapping.getArticleId()));
        assertTrue(foundMapping.isPresent());
        assertEquals("testword", foundMapping.get().getWord());
        assertEquals("[0, 10, 20]", foundMapping.get().getOffsets());
    }

    @Test
    void testUpdateWordsMapping() {
        // First, save an article
        Article article = new Article();
        article.setName("Test Article for Words");
        article.setStatus(ArticleStatus.pending);
        article.setSize(100);
        article.setAuthor("Author B");
        Article savedArticle = articleRepository.save(article);

        // Now save a words mapping
        WordsMapping wordsMapping = new WordsMapping();
        wordsMapping.setWord("testword");
        wordsMapping.setArticleId(savedArticle.getId());
        wordsMapping.setOffsets("[0, 10, 20]");
        WordsMapping savedMapping = wordsMappingRepository.save(wordsMapping);

        // Update the word mapping
        savedMapping.setOffsets("[0, 15, 30]");
        WordsMapping updatedMapping = wordsMappingRepository.save(savedMapping);

        Optional<WordsMapping> foundMapping = wordsMappingRepository.findById(new WordsMappingKey(savedMapping.getWord(), savedMapping.getArticleId()));
        assertTrue(foundMapping.isPresent());
        assertEquals("[0, 15, 30]", foundMapping.get().getOffsets());
    }

    @Test
    void testDeleteWordsMapping() {
        // First, save an article
        Article article = new Article();
        article.setName("Test Article for Words");
        article.setStatus(ArticleStatus.pending);
        article.setSize(100);
        article.setAuthor("Author B");
        Article savedArticle = articleRepository.save(article);

        // Now save a words mapping
        WordsMapping wordsMapping = new WordsMapping();
        wordsMapping.setWord("testword");
        wordsMapping.setArticleId(savedArticle.getId());
        wordsMapping.setOffsets("[0, 10, 20]");
        WordsMapping savedMapping = wordsMappingRepository.save(wordsMapping);

        // Delete the word mapping
        wordsMappingRepository.delete(savedMapping);

        Optional<WordsMapping> foundMapping = wordsMappingRepository.findById(new WordsMappingKey(savedMapping.getWord(), savedMapping.getArticleId()));
        assertFalse(foundMapping.isPresent());  // Ensure the mapping was deleted
    }
}
