package com.example.demo.controllers;

import com.example.demo.models.User;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public User register(@RequestParam String name, @RequestParam String email) {
        return userService.registerUser(name, email);
    }

    @PostMapping("/login")
    public User login(@RequestParam String email) {
        return userService.loginUser(email);
    }
}
