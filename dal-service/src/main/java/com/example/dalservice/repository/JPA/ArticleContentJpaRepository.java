package com.example.dalservice.repository.JPA;

import com.example.dalservice.entity.ArticleContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleContentJpaRepository extends JpaRepository<ArticleContent,Long> {
    // Find the article content with the largest size
    @Query(value = "SELECT * FROM article_content ORDER BY LENGTH(compressed_content) DESC LIMIT 1", nativeQuery = true)
    Optional<ArticleContent> findTopByOrderByCompressedContentLengthDesc();

    // Find the article content with the smallest size
    @Query(value = "SELECT * FROM article_content ORDER BY LENGTH(compressed_content) ASC LIMIT 1", nativeQuery = true)
    Optional<ArticleContent> findTopByOrderByCompressedContentLengthAsc();


}
