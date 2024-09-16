package com.example.dalservice.controller;
import com.example.dalservice.dto.ArticleCreationDTO;
import com.example.dalservice.dto.ArticleContentDTO;
import com.example.dalservice.Service.ArticleService;
import com.example.dalservice.Service.ArticleContentService;
import com.example.dalservice.entity.Article;
import com.example.dalservice.entity.ArticleContent;
import com.example.dalservice.entity.ArticleStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final ArticleContentService articleContentService;

    // Constructor-based dependency injection
    public ArticleController(ArticleService articleService, ArticleContentService articleContentService) {
        this.articleService = articleService;
        this.articleContentService = articleContentService;
    }

    // DTO for adding a new article (metadata and content)
//    public static class ArticleCreationDTO {
//        private String name;
//        private String author;
//        private long size;
//        private String status;
//        private byte[] content; // For storing the binary content of the article
//
//        // Getters and Setters
//        public String getName() {return name;}
//        public String getAuthor() {
//            return author;
//        }
//        public long getSize() {
//            return size;
//        }
//        public String getStatus() {
//            return status;
//        }
//        public byte[] getContent() {
//            return content;
//        }
//
//    }

    // Add a new article (metadata and content)
    @Operation(summary = "Create a new article", description = "Add a new article along with its content to the database")
    @ApiResponse(responseCode = "201", description = "Article created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid article status")
    @ApiResponse(responseCode = "409", description = "Conflict - article with the same name and author already exists")
    @PostMapping("/create")
    public ResponseEntity<String> addArticle(@RequestBody ArticleCreationDTO articleDTO) {
        try {
            // Convert the status to an enum (lowercase is handled for flexibility)
            ArticleStatus status;
            try {
                status = ArticleStatus.valueOf(articleDTO.getStatus().toLowerCase());
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>("Invalid status value: " + articleDTO.getStatus(), HttpStatus.BAD_REQUEST);
            }
            // Create and save the article
            Article article = new Article(
                    articleDTO.getName(),
                    articleDTO.getAuthor(),
                    articleDTO.getSize(),
                    status
            );
            Article savedArticle = articleService.saveArticle(article);
            // Create and save the article content

            ArticleContent content = new ArticleContent(savedArticle, articleDTO.getContent());
            articleContentService.saveArticleContent(content);

            return new ResponseEntity<>("Article created successfully with ID: " + savedArticle.getId(), HttpStatus.CREATED);
        }catch (DataIntegrityViolationException e) {
            // Handle cases like duplicate entries
            return new ResponseEntity<>("Conflict: Article with the same name and author already exists.", HttpStatus.CONFLICT);
        } catch (Exception e) {
            // Catch any other exception and return a generic error
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get an article's metadata by its ID
    @Operation(summary = "Get an article's metadata by ID", description = "Retrieve metadata of a specific article by its ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved article metadata")
    @ApiResponse(responseCode = "404", description = "Article not found")
    @GetMapping("/{id}/metadata")
    public ResponseEntity<ArticleCreationDTO> getArticleMetadataById(@PathVariable Long id) {
        Optional<Article> articleOptional = articleService.getArticleById(id);
        if (!articleOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Article article = articleOptional.get();
        ArticleCreationDTO dto = new ArticleCreationDTO();
        dto.setName(article.getName());
        dto.setAuthor(article.getAuthor());
        dto.setSize(article.getSize());
        dto.setStatus(article.getStatus().name());

        // Retrieve the content if available
        Optional<ArticleContent> contentOptional = articleContentService.getArticleContentById(id);
        if (contentOptional.isPresent()) {
            dto.setContent(contentOptional.get().getCompressedContent());
        } else {
            // If content is not found, you can decide how to handle it.
            dto.setContent(null); // or handle it based on your application's needs
        }

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    // Get article content by ID
    @Operation(summary = "Get article content by ID", description = "Retrieve the content of a specific article by its ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved article content")
    @ApiResponse(responseCode = "404", description = "Article content not found")
    @GetMapping("/{id}/content")
    public ResponseEntity<ArticleContentDTO> getArticleContentById(@PathVariable Long id) {
        Optional<ArticleContent> contentOptional  = articleContentService.getArticleContentById(id);
        if (!contentOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ArticleContent content = contentOptional.get();
        ArticleContentDTO dto = new ArticleContentDTO();
        dto.setContent(content.getCompressedContent());

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    // Delete an article by ID (cascading will delete content and mappings)
    @Operation(summary = "Delete an article by ID", description = "Delete an article by its ID, and cascade delete related content and word mappings")
    @ApiResponse(responseCode = "204", description = "Article deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Get the most recent article
    @Operation(summary = "Get the most recent article", description = "Retrieve the most recently added article")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the most recent article")
    @ApiResponse(responseCode = "404", description = "No articles found")
    @GetMapping("/recent")
    public ResponseEntity<ArticleCreationDTO> getMostRecentArticle() {
        Optional<Article> articleOptional  = articleService.getMostRecentArticle();
        if (!articleOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Article article = articleOptional.get();
        ArticleCreationDTO dto = convertToDTO(article);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    // Get the oldest article
    @Operation(summary = "Get the oldest article", description = "Retrieve the oldest article in the database")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the oldest article")
    @ApiResponse(responseCode = "404", description = "No articles found")
    @GetMapping("/oldest")
    public ResponseEntity<ArticleCreationDTO> getOldestArticle() {
        Optional<Article> articleOptional  = articleService.getOldestArticle();
        if (!articleOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Article article = articleOptional.get();
        ArticleCreationDTO dto = convertToDTO(article);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    // Get the longest article (by size)
    @Operation(summary = "Get the longest article", description = "Retrieve the article with the longest content")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the longest article")
    @ApiResponse(responseCode = "404", description = "No articles found")
    @GetMapping("/longest")
    public ResponseEntity<ArticleCreationDTO> getLongestArticle() {
        Optional<ArticleContent> articleOptional  = articleContentService.getLongestArticleContent();
        if (!articleOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ArticleContent article = articleOptional.get();
        ArticleCreationDTO dto = convertToDTO(article.getArticle());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    // Get the shortest article (by size)
    @Operation(summary = "Get the shortest article", description = "Retrieve the article with the shortest content")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the shortest article")
    @ApiResponse(responseCode = "404", description = "No articles found")
    @GetMapping("/shortest")
    public ResponseEntity<ArticleCreationDTO> getShortestArticle() {
        Optional<ArticleContent> articleOptional  = articleContentService.getShortestArticleContent();
        if (!articleOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ArticleContent article = articleOptional.get();
        ArticleCreationDTO dto = convertToDTO(article.getArticle());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    private ArticleCreationDTO convertToDTO(Article article) {
        ArticleCreationDTO dto = new ArticleCreationDTO();
        dto.setId(article.getId());
        dto.setName(article.getName());
        dto.setAuthor(article.getAuthor());
        dto.setCreatedAt(article.getCreatedAt());
        dto.setSize(article.getSize());
        dto.setStatus(article.getStatus().name());

        // Fetch article content if needed
        Optional<ArticleContent> content = articleContentService.getArticleContentById(article.getId());
        content.ifPresent(articleContent -> dto.setContent(articleContent.getCompressedContent()));

        return dto;
    }
}
