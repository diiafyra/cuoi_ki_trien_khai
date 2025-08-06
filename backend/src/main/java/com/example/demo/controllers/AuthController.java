package com.example.demo.controllers;

import com.example.demo.DTOs.*;
import com.example.demo.models.User;
import com.example.demo.services.AuthService;
import com.google.firebase.auth.FirebaseAuthException;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(ApiResponse.success("Registration successful", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Login failed: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            User user = authService.getUserByToken(token);

            if (user != null) {
                authService.logout(user.getId());
                return ResponseEntity.ok(ApiResponse.success("Logout successful", "User logged out"));
            } else {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid token"));
            }
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid token: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Logout failed: " + e.getMessage()));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<User>> getProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            User user = authService.getUserByToken(token);

            if (user != null) {
                return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", user));
            } else {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("User not found"));
            }
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid token: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get profile: " + e.getMessage()));
        }
    }

    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> request) {
        try {
            String idToken = request.get("idToken");
            System.out.println("📥 Nhận idToken từ client: " + idToken);

            AuthResponse authResponse = authService.loginWithGoogle(idToken);

            System.out.println("✅ Đăng nhập Google thành công, trả về AuthResponse: " + authResponse);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            e.printStackTrace(); // In stacktrace để dễ tìm lỗi hơn
            System.err.println("❌ Đăng nhập Google thất bại: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Google login failed: " + e.getMessage());
        }
    }

    @GetMapping("/verify-token")
    public ResponseEntity<ApiResponse<String>> verifyToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            User user = authService.getUserByToken(token);

            if (user != null) {
                return ResponseEntity.ok(ApiResponse.success("Token is valid", "Token verified"));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid token"));
            }
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid token: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Token verification failed: " + e.getMessage()));
        }
    }
}