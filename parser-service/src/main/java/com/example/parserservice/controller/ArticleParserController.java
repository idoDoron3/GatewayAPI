package com.example.parserservice.controller;


import com.example.parserservice.DTO.ArticleContentDTO;
import com.example.parserservice.DTO.ParsedWordMappingDTO;
import com.example.parserservice.service.ArticleParserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/parser")
public class ArticleParserController {
    private final ArticleParserService articleParserService;

    public ArticleParserController(ArticleParserService articleParserService) {
        this.articleParserService = articleParserService;
    }
    // POST endpoint to parse article content and return word mappings
    @PostMapping("/parse")
    public ResponseEntity<List<ParsedWordMappingDTO>> parseArticle(@RequestBody ArticleContentDTO articleContentDTO) {
        try {
            // Parse the article content
            List<ParsedWordMappingDTO> wordMappings = articleParserService.parseArticle(articleContentDTO.getContent(), articleContentDTO.getArticleId());

            // Return the parsed word mappings as the response
            return new ResponseEntity<>(wordMappings, HttpStatus.OK);
        } catch (Exception e) {
            // In case of any error, return a bad request status
            System.err.println("Error parsing article content: " + e.getMessage());

            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
