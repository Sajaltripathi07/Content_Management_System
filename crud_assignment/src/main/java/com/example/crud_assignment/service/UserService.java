package com.example.crud_assignment.service;

import com.example.crud_assignment.model.User;

public interface UserService {
    User register(User user);

    String login(String username, String password);

    User getByToken(String token);
} 