package com.example.crud_assignment.service;

import com.example.crud_assignment.model.Article;
import com.example.crud_assignment.repository.ArticleRepository;
import com.example.crud_assignment.service.impl.ArticleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ArticleServiceTest {
    private ArticleRepository repo;
    private ArticleService service;

    @BeforeEach
    void setUp() {
        repo = mock(ArticleRepository.class);
        service = new ArticleServiceImpl(repo);
    }

    @Test
    void testCreate() {
        Article article = new Article();
        article.setTitle("Test");
        when(repo.save(any(Article.class))).thenReturn(article);
        Article saved = service.create(article);
        assertEquals("Test", saved.getTitle());
    }

    @Test
    void testList() {
        when(repo.findAll()).thenReturn(Collections.emptyList());
        assertTrue(service.list().isEmpty());
    }
} 