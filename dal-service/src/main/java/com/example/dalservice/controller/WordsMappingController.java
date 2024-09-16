package com.example.dalservice.controller;

import com.example.dalservice.Service.WordsMappingService;
import com.example.dalservice.entity.WordsMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/word-mappings")
public class WordsMappingController {

    private final WordsMappingService wordsMappingService;

    public WordsMappingController(WordsMappingService wordsMappingService) {
        this.wordsMappingService = wordsMappingService;
    }

    // Get all word mappings
    @Operation(summary = "Get all word mappings", description = "Retrieve a list of all word mappings in the database")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all word mappings")
    @GetMapping("/all")
    public ResponseEntity<List<WordsMapping>> getAllWordsMappings() {
        List<WordsMapping> wordMappings = wordsMappingService.getAllWordsMappings();
        if (wordMappings.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // No content if list is empty
        }
        return new ResponseEntity<>(wordMappings, HttpStatus.OK);
    }

    // Find word mappings by word
    @Operation(summary = "Find word mappings by word", description = "Search for word mappings by the specified word")
    @ApiResponse(responseCode = "200", description = "Successfully found word mappings")
    @ApiResponse(responseCode = "404", description = "No word mappings found for the given word")
    @GetMapping("/find/{word}")
    public ResponseEntity<?> findWordsByWord(@PathVariable String word) {
        try {
            // Perform case-insensitive search for the word
            List<WordsMapping> result = wordsMappingService.findWordsByWord(word);

            // Check if the result is empty, and throw a custom message if no result is found
            if (result.isEmpty()) {
                return new ResponseEntity<>("No results found for the word: " + word, HttpStatus.NOT_FOUND);
            }
            // Return the result if found
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error occurred while searching for the word.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Save multiple word mappings
    @Operation(summary = "Save multiple word mappings", description = "Save a list of word mappings to the database")
    @ApiResponse(responseCode = "201", description = "Word mappings created successfully")
    @ApiResponse(responseCode = "404", description = "Article not found")
    @PostMapping("/save-all")
    public ResponseEntity<String> saveAllWordsMappings(@RequestBody List<WordsMapping> wordMappings) {
        try {
            // Check if articleId is null in the incoming wordMappings
            for (WordsMapping mapping : wordMappings) {
                if (mapping.getArticleId() == null) {
                    throw new IllegalArgumentException("Article ID must not be null");
                }
            }
            wordsMappingService.saveAllWordsMappings(wordMappings);
            return new ResponseEntity<>("Word mappings saved and article status updated to indexed", HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
