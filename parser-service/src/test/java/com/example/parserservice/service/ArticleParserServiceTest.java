package com.example.parserservice.service;
import com.example.parserservice.DTO.ParsedWordMappingDTO;
import com.example.parserservice.service.ArticleParserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ArticleParserServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ArticleParserService articleParserService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize Mockito annotations before each test
    }

    // Test small content that should trigger single-threaded parsing
    @Test
    public void testParseSmallContentSingleThreaded() throws ExecutionException, InterruptedException {
        String content = "Hello world. Welcome to the parser test.";
        Long articleId = 1L;

        List<ParsedWordMappingDTO> result = articleParserService.parseArticle(content, articleId);

        // Verify that the result contains the expected number of unique words
        assertEquals(7, result.size());

        // Ensure specific words and their corresponding offsets are present
        assertTrue(result.stream().anyMatch(w -> w.getWord().equals("hello")));
        assertTrue(result.stream().anyMatch(w -> w.getWord().equals("world")));
    }

    // Test large content that should trigger multi-threaded parsing
    @Test
    public void testParseLargeContentMultiThreadedWithCaseVariations() throws ExecutionException, InterruptedException {
        // Generate content with case variations for words from word0 to word499
        StringBuilder contentBuilder = new StringBuilder();
        for (int i = 0; i < 500; i++) {
            contentBuilder.append("word").append(i).append(" ");  // Lowercase
            contentBuilder.append("Word").append(i).append(" ");  // Mixed case
            contentBuilder.append("WORd").append(i).append(" ");  // Uppercase
        }
        String content = contentBuilder.toString();
        Long articleId = 1L;

        List<ParsedWordMappingDTO> result = articleParserService.parseArticle(content, articleId);
        result.forEach(w -> System.out.println("Word: " + w.getWord() + ", Offsets: " + w.getOffsets()));


        // Verify that the result contains the expected number of unique words (500 words)
        assertEquals(1, result.size());

        // Ensure specific words are present (case-insensitive check)
        assertTrue(result.stream().anyMatch(w -> w.getWord().equals("word")));
        assertTrue(result.stream().anyMatch(w -> w.getWord().equals("word")));

    }


    // Test that parsed word mappings are correctly sent to the DAL service
    @Test
    public void testSendParsedDataToDal() {
        List<ParsedWordMappingDTO> wordMappings = List.of(
                new ParsedWordMappingDTO("hello", 1L, "[0]"),
                new ParsedWordMappingDTO("world", 1L, "[6]")
        );

        // Call the method to send data to the DAL
        articleParserService.sendParsedDataToDal(wordMappings);

        // Verify that the RestTemplate postForEntity was called once with the correct URL and payload
        verify(restTemplate, times(1)).postForEntity(anyString(), eq(wordMappings), eq(Void.class));
    }
}
