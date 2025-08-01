package com.example.crud_assignment.repository;

import com.example.crud_assignment.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article,Long> {
    Page<Article> findByUserId(Long userId, Pageable pageable);
}
