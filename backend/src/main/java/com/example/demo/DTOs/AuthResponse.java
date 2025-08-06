package com.example.demo.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO cho response authentication
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String uid;
    private String email;
    private String name;

}
