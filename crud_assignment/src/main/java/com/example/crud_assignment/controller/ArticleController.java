package com.example.crud_assignment.controller;

import com.example.crud_assignment.model.Article;
import com.example.crud_assignment.model.User;
import com.example.crud_assignment.service.ArticleService;
import com.example.crud_assignment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.PageImpl;

@RestController
@RequestMapping("api/articles")
public class ArticleController {

    @Autowired
    private  ArticleService service;
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Article> create(@RequestHeader("token") String token, @RequestBody Article article){
        User user = userService.getByToken(token);
        if (user == null) return ResponseEntity.status(401).build();
        article.setUserId(user.getId());
        return new ResponseEntity<Article>(service.create(article), HttpStatus.CREATED);
    }

    //get by id rest api
    @GetMapping("/{id}")
    public ResponseEntity<Article> get(@RequestHeader("token") String token, @PathVariable Long id){
        User user = userService.getByToken(token);
        if (user == null) return ResponseEntity.status(401).build();
        try {
            return new ResponseEntity<Article>(service.get(id, user.getId()),HttpStatus.OK);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }


    @GetMapping("/recent")
    public List<Long> recent(@RequestHeader("token") String token){
        User user = userService.getByToken(token);
        if (user == null) return List.of();
        return service.getRecentViews(user.getId());
    }

    // getall restapi
    @GetMapping
    public Page<Article> list(@RequestHeader("token") String token,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size) {
        User user = userService.getByToken(token);
        if (user == null) return Page.empty();
        Page<Article> all = service.list(PageRequest.of(page, size));

        //  return articles for  this user only
        List<Article> userArticles = new ArrayList<>();
        for (Article article : all.getContent()) {
            if (article.getUserId().equals(user.getId())) {
                userArticles.add(article);
            }
        }

        return new PageImpl<>(
                userArticles, all.getPageable(), all.getTotalElements());
    }


    @PutMapping("/{id}")
    public ResponseEntity<Article> update(@RequestHeader("token") String token, @PathVariable Long id, @RequestBody Article article){
        User user = userService.getByToken(token);
        if (user == null) return ResponseEntity.status(401).build();
        try {
            // Only allow update if article belongs to user
            Article existing = service.get(id, user.getId());
            if (!existing.getUserId().equals(user.getId())) return ResponseEntity.status(403).build();
            article.setUserId(user.getId());
            return new ResponseEntity<Article>(service.update(id,article),HttpStatus.OK);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@RequestHeader("token") String token, @PathVariable Long id){
        User user = userService.getByToken(token);
        if (user == null) return ResponseEntity.status(401).build();
        try {
            Article existing = service.get(id, user.getId());
            if (!existing.getUserId().equals(user.getId())) return ResponseEntity.status(403).build();
            service.delete(id);
            return new ResponseEntity<String>("Article deleted Successfully.",HttpStatus.OK);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }




}
