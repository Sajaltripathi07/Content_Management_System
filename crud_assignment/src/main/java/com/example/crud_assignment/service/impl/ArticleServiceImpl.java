package com.example.crud_assignment.service.impl;

import com.example.crud_assignment.model.Article;
import com.example.crud_assignment.repository.ArticleRepository;
import com.example.crud_assignment.service.ArticleService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepository repo;
    private final Map<Long, LinkedList<Long>> recentViews= new HashMap<>();
    private static final int Max_History= 5;

    public ArticleServiceImpl(ArticleRepository repo) {
        this.repo = repo;
    }

    @Override
    public Article create(Article article) {
        return repo.save(article);
    }

    @Override
    public Article get(Long id, Long userId) {
        Article article = repo.findById(id).orElseThrow(
                ()-> new RuntimeException("Not found"));


        //tracking recently viewed
       recentViews.putIfAbsent(userId,new LinkedList<>());
       LinkedList<Long> history = recentViews.get(userId);
       history.remove(id);
       history.addFirst(id);
       if(history.size()> Max_History){
           history.removeLast();
       }
       return article;
    }

    @Override
    public List<Long> getRecentViews(Long userId) {
        return recentViews.getOrDefault(userId, new LinkedList<>());
    }

    @Override
    public List<Article> list() {
        return repo.findAll();
    }

    @Override
    public Page<Article> list(Pageable pageable) {
        return repo.findAll(pageable);
    }

    // Add this method for user-specific pagination
    @Override
    public Page<Article> listByUser(Long userId, Pageable pageable) {
        return repo.findByUserId(userId, pageable);
    }

    @Override
    public Article update(Long id, Article updated) {
        Article existingArticle = repo.findById(id).orElseThrow(
                ()-> new RuntimeException("Not found"));

        existingArticle.setTitle(updated.getTitle());
        existingArticle.setContent(updated.getContent());
        existingArticle.setAuthor(updated.getAuthor());

        return repo.save(existingArticle);
    }

    @Override
    public void delete(Long id) {
     repo.deleteById(id);
    }
}
