package com.example.dalservice.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "article_content")// Maps this class to the 'article_content' table in the database
public class ArticleContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleId;

    @OneToOne
    @MapsId  // The article_id shares the same value as the primary key of the article
    @JoinColumn(name = "article_id")
    private Article article;  // Foreign key reference to the 'articles' table

    @Lob //large object - use byte[]
    private byte[] compressedContent;  // Stores the article content in binary (BLOB format)
    public ArticleContent() {}

    public ArticleContent(Article article, byte[] compressedContent) {
        this.article = article;
        this.compressedContent = compressedContent;
    }
    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public void setCompressedContent(byte[] compressedContent) {
        this.compressedContent = compressedContent;
    }
    public Long getArticleId() {
        return articleId;
    }

    public Article getArticle() {
        return article;
    }
    public byte[] getCompressedContent() {
        return compressedContent;
    }



}
