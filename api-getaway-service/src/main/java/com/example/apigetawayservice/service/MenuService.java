package com.example.apigetawayservice.service;

import com.example.apigetawayservice.dto.ArticleContentDTO;
import com.example.apigetawayservice.dto.ArticleCreationDTO;
import com.example.apigetawayservice.dto.CombinedArticleDTO;
import com.example.apigetawayservice.dto.WordsMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

@Service
public class MenuService {

    private final RestTemplate restTemplate;
    // Constructor injection for RestTemplate
    @Autowired
    public MenuService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Utility to check if a string is a positive number (digit)
    private boolean isPositiveNumber(String input) {
        return input.matches("\\d+"); // Matches only digits (positive integers)
    }

    // Utility to check if a string contains only valid word characters (alphabet)
    // Utility to check if a string contains only valid word characters (alphabet)
    private boolean isValidWord(String input) {
        return input.matches("[a-zA-Z]+"); // Matches only letters (no numbers or special characters)
    }

    // Utility to check if a string is not empty
    private boolean isNotEmpty(String input) {
        return input != null && !input.trim().isEmpty();
    }

    public void createArticle(Scanner scanner) {
        System.out.print("Enter article name: ");
        String name = scanner.nextLine();

        System.out.print("Enter author name: ");
        String author = scanner.nextLine();
        if (!isNotEmpty(author) || !isValidWord(author)) {
            System.out.println("Invalid author name. It must contain only alphabetic characters and cannot be empty.");
            return;
        }

        System.out.print("Enter article content: ");
        String content = scanner.nextLine();
        if (!isNotEmpty(content)) {
            System.out.println("Invalid content. It cannot be empty.");
            return;
        }
        byte[] contentBytes = content.getBytes();

        long size = content.length();  // Calculate size

        ArticleCreationDTO article = new ArticleCreationDTO(name, author, size, "pending", contentBytes);
//        String response = restTemplate.postForObject("http://localhost:8081/api/article/create", article, String.class);
//        System.out.println("Response: " + response);
        try {
            // Step 1: Send request to the DAL service to create the article
            ResponseEntity<String> dalResponse = restTemplate.postForEntity(
                    "http://localhost:8081/api/article/create", article, String.class);//TODO env variables

            if (dalResponse.getStatusCode().is2xxSuccessful() && dalResponse.getBody() != null) {
                // Step 2: Extract article ID from the response string
                String responseMessage = dalResponse.getBody();
                Long articleId = extractArticleIdFromResponse(responseMessage);  // Custom method to extract ID

                if (articleId != null) {
                    // Step 3: Send request to the Parser service to parse the article content
                    ArticleContentDTO articleContentDTO = new ArticleContentDTO(articleId, content);
                    String parserResponse = restTemplate.postForObject(
                            "http://localhost:8082/api/parser/parse", articleContentDTO, String.class);

                    System.out.println("Article created successfully with ID: " + articleId);
                    System.out.println("Parser service response: " + parserResponse);
                } else {
                    System.out.println("Failed to extract article ID from DAL response.");
                }
            } else {
                System.out.println("Failed to create article in DAL service.");
            }
        } catch (RestClientException e) {
            System.out.println("An error occurred while creating the article: " + e.getMessage());
        }
    }

    private Long extractArticleIdFromResponse(String response) {
        try {
            // Assuming the response contains something like "Article created successfully with ID: {id}"
            String[] parts = response.split(":");
            return Long.parseLong(parts[1].trim());
        } catch (Exception e) {
            // Log the error and return null if parsing fails
            System.out.println("Error parsing article ID from response: " + e.getMessage());
            return null;
        }
    }

    public void getArticleById(Scanner scanner) {
        System.out.print("Enter article ID: ");
        String articleIdInput = scanner.nextLine();
        if (!isPositiveNumber(articleIdInput)) {
            System.out.println("Invalid Article ID. Please enter a positive number.");
            return;
        }

        long articleId = Long.parseLong(articleIdInput);

        try {
            // Fetch article metadata
            ArticleCreationDTO articleMetadata = restTemplate.getForObject("http://localhost:8081/api/article/{id}/metadata", ArticleCreationDTO.class, articleId);

            if (articleMetadata == null) {
                System.out.println("No article found with the given ID.");
                return;
            }

            // Fetch article content
            ArticleContentDTO articleContent = restTemplate.getForObject("http://localhost:8081/api/article/{id}/content", ArticleContentDTO.class, articleId);
            CombinedArticleDTO combinedArticle = combineArticleData(articleMetadata, articleContent);
            printArticle(combinedArticle);
            // Combine metadata and content into a DTO

        } catch (RestClientException e) {
            System.out.println("An error occurred while retrieving the article: " + e.getMessage());
        }
    }

    public void findWordsInArticle(Scanner scanner) {
        System.out.print("Enter word to search: ");
        String word = scanner.nextLine();
        if (!isNotEmpty(word) || !isValidWord(word)) {
            System.out.println("Invalid word. Please enter a valid word containing only alphabetic characters.");
            return;
        }
        try {
            // Use exchange() with ParameterizedTypeReference for handling List<WordsMapping>
            ResponseEntity<List<WordsMapping>> responseEntity = restTemplate.exchange(
                    "http://localhost:8081/api/dal/find/{word}",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<WordsMapping>>() {},
                    word
            );

            List<WordsMapping> wordMappings = responseEntity.getBody();

            if (wordMappings != null && !wordMappings.isEmpty()) {
                System.out.println("Word mappings found:");
                for (WordsMapping mapping : wordMappings) {
                    System.out.println("Word: " + mapping.getWord());
                    System.out.println("Article ID: " + mapping.getArticleId());
                    System.out.println("Offsets: " + mapping.getOffsets());
                    System.out.println("--------------------------");
                }
            } else {
                System.out.println("No word mappings found for the word: " + word);
            }

        } catch (RestClientException e) {
            System.out.println("Error occurred while searching for the word: " + e.getMessage());
        }
    }


    public void deleteArticleById(Scanner scanner) {
        System.out.print("Enter article ID to delete: ");
        String articleIdInput = scanner.nextLine();

        if (!isPositiveNumber(articleIdInput)) {
            System.out.println("Invalid Article ID. Please enter a positive number.");
            return;
        }

        long articleId = Long.parseLong(articleIdInput);
        try {
            // Pass the article ID as a path variable in the delete request
            restTemplate.delete("http://localhost:8081/api/article/{id}", articleId);
            System.out.println("Article deleted successfully.");
        } catch (RestClientException e) {
            // Handle the error gracefully
            System.out.println("Error occurred while deleting the article: " + e.getMessage());
        }
    }


    private CombinedArticleDTO combineArticleData(ArticleCreationDTO metadata, ArticleContentDTO content) {
        CombinedArticleDTO combinedArticle = new CombinedArticleDTO();
        combinedArticle.setId(metadata.getId());
        combinedArticle.setName(metadata.getName());
        combinedArticle.setAuthor(metadata.getAuthor());
        combinedArticle.setCreatedAt(metadata.getCreatedAt());
        combinedArticle.setSize(metadata.getSize());
        combinedArticle.setStatus(metadata.getStatus().toString());

        if (content != null && content.getContent() != null) {
            String articleContent = content.getContent();
            combinedArticle.setContent(articleContent);
        } else {
            combinedArticle.setContent("No content available.");
        }

        return combinedArticle;
    }

    // Helper method to print article details
    private void printArticle(CombinedArticleDTO article) {
        System.out.println("---Article---:");
        System.out.println("ID: " + article.getId());
        System.out.println("Name: " + article.getName());
        System.out.println("Author: " + article.getAuthor());
        System.out.println("Created At: " + article.getCreatedAt());
        System.out.println("Status: " + article.getStatus());
        System.out.println("Content: " + article.getContent());
        System.out.println("--------------------------");
    }
}
