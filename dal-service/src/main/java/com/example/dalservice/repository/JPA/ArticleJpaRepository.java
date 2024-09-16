package com.example.dalservice.repository.JPA;

import com.example.dalservice.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ArticleJpaRepository extends JpaRepository<Article,Long> {
    // Find the most recent article by creation date
    Optional<Article> findTopByOrderByCreatedAtDesc();

    // Find the oldest article by creation date
    Optional<Article> findTopByOrderByCreatedAtAsc();

    Optional<Article> findByNameAndAuthor(String name, String author);




}
