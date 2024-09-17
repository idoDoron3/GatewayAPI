package com.example.apigatewayservice.service;

import com.example.apigatewayservice.dto.ArticleDTO;
import com.example.apigatewayservice.dto.ArticleContentDTO;
import com.example.apigatewayservice.dto.WordsMapping;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
        // register JavaTimeModule to handle LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());
        // configure objectMapper to format dates
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public ResponseEntity<String> addArticle(ArticleDTO articleDTO) {
        if (!validateArticleDTO(articleDTO)) {
            return ResponseEntity.badRequest().body("Invalid input data");
        }
        String url = articleServiceUrl + "/api/articles/create";

        ResponseEntity<String> response = restTemplate.postForEntity(url, articleDTO, String.class);

        if (response.getStatusCode() == HttpStatus.CREATED) {
            String responseBody = response.getBody();
            // extract the generated ID from the response -json format
            Long articleId = extractArticleId(responseBody);
            // send to parser service
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
        ResponseEntity<ArticleDTO> response = restTemplate.getForEntity(url, ArticleDTO.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            ArticleDTO dto = response.getBody();
            try {
                // Convert dto to json string using ObjectMapper
                ObjectNode jsonNode = objectMapper.valueToTree(dto);
                // remove the "content" field
                jsonNode.remove("content");
                // convert back to json string
                String jsonString = objectMapper.writeValueAsString(jsonNode);
                return new ResponseEntity<>(jsonString, HttpStatus.OK);
            } catch (Exception e) {
                // handle json conversion errors
                return new ResponseEntity<>("Error converting metadata to JSON", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            // Return 404
            return new ResponseEntity<>("Article not found", HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> getArticleContent(Long id) {
        String url = articleServiceUrl + "/api/articles/" + id + "/content";
        ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            try {
                // parse json response
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                // extract the encoded content
                String base64Content = jsonNode.path("content").asText();
                // decode content to get the original string
                byte[] decodedBytes = Base64.getDecoder().decode(base64Content);
                String content = new String(decodedBytes, StandardCharsets.UTF_8);

                // return the decoded content as response
                return new ResponseEntity<>(content, HttpStatus.OK);
            } catch (Exception e) {
                // handle json parsing and Base64 decoding errors
                return new ResponseEntity<>("Error processing content", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }  else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            // return 404
            return new ResponseEntity<>("Article content not found", HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

public ResponseEntity<Map<String, Object>> getWordMappings(String word) {
    //  url to call the dal service
    String url = articleServiceUrl + "/api/word-mappings/find/" + word;
    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
        // if no word mappings are found, return a 404
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    try {
        ObjectMapper objectMapper = new ObjectMapper();
        List<WordsMapping> wordMappings = objectMapper.readValue(response.getBody(), new TypeReference<List<WordsMapping>>() {});
        Map<String, Object> formattedResponse = new LinkedHashMap<>();
        formattedResponse.put("word", word);
        // prepare the locations
        List<Map<String, Object>> locations = new ArrayList<>();
        for (WordsMapping mapping : wordMappings) {
            Map<String, Object> location = new HashMap<>();
            location.put("article_id", mapping.getArticleId());
            location.put("offsets", mapping.getOffsets()); // Just return the offsets as a string
            locations.add(location);
        }

        formattedResponse.put("locations", locations);

        // return the formatted response
        return new ResponseEntity<>(formattedResponse, HttpStatus.OK);

    } catch (Exception e) {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}



    public ResponseEntity<String> deleteArticle(Long id) {
        String url = articleServiceUrl + "/api/articles/" + id;
        restTemplate.delete(url);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // validate the articleCreationDTO input
    private boolean validateArticleDTO(ArticleDTO articleDTO) {
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
        // extract the article ID from the response body
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
        String sanitized = word.replaceAll("[^a-zA-Z]", " ").split("\\s+")[0].toLowerCase();
        if (sanitized.isEmpty()) {
            throw new IllegalArgumentException("Invalid word input.");
        }
        return sanitized;
    }


}
