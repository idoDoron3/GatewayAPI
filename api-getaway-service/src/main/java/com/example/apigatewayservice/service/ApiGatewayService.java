package com.example.apigatewayservice.service;

import com.example.apigatewayservice.dto.ArticleCreationDTO;
import com.example.apigatewayservice.dto.ArticleContentDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@Service
public class ApiGatewayService {
    private final RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    @Value("${dal.service.url}")
    private String articleServiceUrl;

    @Value("${parser.service.url}")
    private String parserServiceUrl;

    public ApiGatewayService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        objectMapper = new ObjectMapper();
        // Register JavaTimeModule to handle LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());
        // Configure ObjectMapper to format dates in ISO-8601
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public ResponseEntity<String> addArticle(ArticleCreationDTO articleDTO) {
        if (!validateArticleDTO(articleDTO)) {
            return ResponseEntity.badRequest().body("Invalid input data");
        }
        String url = articleServiceUrl + "/api/articles/create";

        ResponseEntity<String> response = restTemplate.postForEntity(url, articleDTO, String.class);

        if (response.getStatusCode() == HttpStatus.CREATED) {
            String responseBody = response.getBody();
            // Extract the generated ID from the response (JSON format)
            Long articleId = extractArticleId(responseBody);
            System.out.println(articleId);
            // Send the article content to the parser service
            String contentString = new String(articleDTO.getContent(), StandardCharsets.UTF_8);
            ArticleContentDTO contentDTO = new ArticleContentDTO(articleId, contentString);
            String parserUrl = parserServiceUrl + "/api/parser/parse";

            ResponseEntity<String> parserResponse = restTemplate.postForEntity(parserUrl, contentDTO, String.class);
            return parserResponse;
        }
        return response;
    }

    public ResponseEntity<String> getArticleMetadata(Long id) {
        String url = articleServiceUrl + "/api/articles/" + id + "/metadata";
        ResponseEntity<ArticleCreationDTO> response = restTemplate.getForEntity(url, ArticleCreationDTO.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            ArticleCreationDTO dto = response.getBody();
            try {
                // Convert DTO to JSON string using ObjectMapper
                ObjectNode jsonNode = objectMapper.valueToTree(dto);
                // Remove the "content" field
                jsonNode.remove("content");
                // Convert back to JSON string
                String jsonString = objectMapper.writeValueAsString(jsonNode);
                return new ResponseEntity<>(jsonString, HttpStatus.OK);
            } catch (Exception e) {
                // Handle JSON conversion errors
                return new ResponseEntity<>("Error converting metadata to JSON", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            // Return 404 Not Found if the article or metadata does not exist
            return new ResponseEntity<>("Article not found", HttpStatus.NOT_FOUND);
        } else {
            // Handle other possible errors
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> getArticleContent(Long id) {
        String url = articleServiceUrl + "/api/articles/" + id + "/content";
        ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            try {
                // Parse JSON response
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.getBody());

                // Extract the Base64-encoded content
                String base64Content = jsonNode.path("content").asText();

                // Decode Base64 content to get the original string
                byte[] decodedBytes = Base64.getDecoder().decode(base64Content);
                String content = new String(decodedBytes, StandardCharsets.UTF_8);

                // Return the decoded content as response
                return new ResponseEntity<>(content, HttpStatus.OK);
            } catch (Exception e) {
                // Handle JSON parsing and Base64 decoding errors
                return new ResponseEntity<>("Error processing content", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }  else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            // Return 404 Not Found if the article doesn't exist
            return new ResponseEntity<>("Article content not found", HttpStatus.NOT_FOUND);
        } else {
            // Handle other error cases
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> getWordMappings(String word) {
        String url = articleServiceUrl + "/api/word-mappings/find/" + word;
        return restTemplate.getForEntity(url, String.class);
    }

    public ResponseEntity<String> deleteArticle(Long id) {
        String url = articleServiceUrl + "/api/articles/" + id;
        restTemplate.delete(url);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Validate the ArticleCreationDTO input
    private boolean validateArticleDTO(ArticleCreationDTO articleDTO) {
        if (articleDTO == null) {
            return false;
        }
        if (articleDTO.getName() == null || articleDTO.getName().isEmpty()) {
            return false;
        }
        if (articleDTO.getContent() == null || articleDTO.getContent().length == 0) {
            return false;
        }
        return true;
    }
    private Long extractArticleId(String responseBody) {
        // Extract the article ID from the response body (implement as needed)
        return Long.parseLong(responseBody.replaceAll("[^0-9]", ""));
    }

    public Long validateAndConvertId(String idStr) {
        try {
            Long id = Long.parseLong(idStr);
            if (id <= 0) {
                throw new IllegalArgumentException("Invalid article ID.");
            }
            return id;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid article ID format.");
        }
    }

    public String sanitizeWord(String word) {
        String sanitized = word.replaceAll("[^a-z]", "").toLowerCase();
        if (sanitized.isEmpty()) {
            throw new IllegalArgumentException("Invalid word input.");
        }
        return sanitized;
    }


}
