package com.example.crud_assignment.service.impl;

import com.example.crud_assignment.model.User;
import com.example.crud_assignment.repository.UserRepository;
import com.example.crud_assignment.service.UserService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepo;
    private final Map<String, User> tokens = new HashMap<>();

    public UserServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public User register(User user) {
        return userRepo.save(user);
    }

    @Override
    public String login(String username, String password) {
        User user = userRepo.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            String token = UUID.randomUUID().toString();
            tokens.put(token, user);
            return token;
        }
        return null;
    }

    @Override
    public User getByToken(String token) {
        return tokens.get(token);
    }
} 