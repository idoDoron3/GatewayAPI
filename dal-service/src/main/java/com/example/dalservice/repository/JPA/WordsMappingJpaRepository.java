package com.example.dalservice.repository.JPA;

import java.util.List;
import com.example.dalservice.entity.WordsMapping;
import com.example.dalservice.entity.WordsMappingKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WordsMappingJpaRepository extends JpaRepository<WordsMapping, WordsMappingKey> {
    @Query("SELECT w FROM WordsMapping w WHERE LOWER(w.word) = LOWER(:word)")

    List<WordsMapping> findByWord(@Param("word")String word);

    void deleteByArticleId(Long articleId);

}
