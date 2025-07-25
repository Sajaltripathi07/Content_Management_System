package com.example.crud_assignment.service;

import com.example.crud_assignment.model.Article;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleService {
    Article create(Article article);

    Article get(Long id, Long userId);

    List<Long> getRecentViews(Long userId);

    List<Article> list();

    Page<Article> list(Pageable pageable);

    Page<Article> listByUser(Long userId, Pageable pageable);

    Article update(Long id, Article updated);

    void delete(Long id);
}
