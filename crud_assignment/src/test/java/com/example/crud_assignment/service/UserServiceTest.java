package com.example.crud_assignment.service;

import com.example.crud_assignment.model.User;
import com.example.crud_assignment.repository.UserRepository;
import com.example.crud_assignment.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserRepository repo;
    private UserService service;

    @BeforeEach
    void setUp() {
        repo = mock(UserRepository.class);
        service = new UserServiceImpl(repo);
    }

    @Test
    void testRegister() {
        User user = new User();
        user.setUsername("bob");
        when(repo.save(any(User.class))).thenReturn(user);
        User saved = service.register(user);
        assertEquals("bob", saved.getUsername());
    }

    @Test
    void testLogin() {
        User user = new User();
        user.setUsername("bob");
        user.setPassword("pass");
        when(repo.findByUsername("bob")).thenReturn(user);
        String token = service.login("bob", "pass");
        assertNotNull(token);
    }
} 