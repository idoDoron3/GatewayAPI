package com.example.apigatewayservice.controller;

import com.example.apigatewayservice.dto.ArticleDTO;
import com.example.apigatewayservice.service.ApiGatewayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@EnableAutoConfiguration
@RequestMapping("/api")
public class ApiGatewayController {

    private final RestTemplate restTemplate;
    private final ApiGatewayService apiGatewayService;


    @Value("${dal.service.url}")
    private String articleServiceUrl;

    @Value("${parser.service.url}")
    private String parserServiceUrl;

    public ApiGatewayController(RestTemplate restTemplate,ApiGatewayService apiGatewayService) {
        this.restTemplate = restTemplate;
        this.apiGatewayService = apiGatewayService;
    }

    @Operation(summary = "Create a new article", description = "Add a new article and its content to the system. the content pass as byte array")
    @ApiResponse(responseCode = "201", description = "Successfully created the article")
    @ApiResponse(responseCode = "400", description = "Invalid article input")
    @ApiResponse(responseCode = "409", description = "Conflict - article with the same name and author already exists")
    @PostMapping("/addarticle")
    public ResponseEntity<String> addArticle(@RequestBody ArticleDTO articleDTO) {
        try {
            return apiGatewayService.addArticle(articleDTO);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Get an article's metadata by ID", description = "Retrieve metadata of a specific article by its ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved article metadata")
    @ApiResponse(responseCode = "404", description = "Article not found")
    @GetMapping("/articles/{id}/metadata")
    public ResponseEntity<String> getArticleMetadata(@PathVariable String id) {
        try {
            Long articleId = apiGatewayService.validateAndConvertId(id);
            return apiGatewayService.getArticleMetadata(articleId);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Get article content by ID", description = "Retrieve content of a specific article by its ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved article content")
    @ApiResponse(responseCode = "404", description = "Article not found")
    @GetMapping("/articles/{id}/content")
    public ResponseEntity<String> getArticleContent(@PathVariable String id) {
        try {
            Long articleId = apiGatewayService.validateAndConvertId(id);
            return apiGatewayService.getArticleContent(articleId);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Get word mappings", description = "Retrieve the mappings of a specific word and its occurrences in articles")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved word mappings")
    @ApiResponse(responseCode = "404", description = "Word not found")
    @GetMapping("/word/{word}")
    public ResponseEntity<?> getWordMappings(@PathVariable String word) {
        try {
            String sanitizedWord = apiGatewayService.sanitizeWord(word);
            ResponseEntity<Map<String, Object>> response = apiGatewayService.getWordMappings(sanitizedWord);

            if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                return new ResponseEntity<>("Word not found: " + word, HttpStatus.NOT_FOUND);
            }

            return response;
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Delete an article by ID", description = "Remove an article and its related content from the system by its ID")
    @ApiResponse(responseCode = "204", description = "Successfully deleted the article")
    @ApiResponse(responseCode = "404", description = "Article not found")
    @DeleteMapping("/articles/{id}")
    public ResponseEntity<String> deleteArticle(@PathVariable String id) {
        try {
            Long articleId = apiGatewayService.validateAndConvertId(id);
            apiGatewayService.deleteArticle(articleId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


}
