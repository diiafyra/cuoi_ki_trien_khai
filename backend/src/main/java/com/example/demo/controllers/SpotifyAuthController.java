package com.example.demo.controllers;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.services.SpotifyAuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/spotify")
@RequiredArgsConstructor
public class SpotifyAuthController {

    private final SpotifyAuthService spotifyAuthService;

    // Gọi khi người dùng bấm "Kết nối với Spotify"
    @GetMapping("/login")
    public ResponseEntity<Void> login(@RequestParam String userId) {
        String loginUrl = spotifyAuthService.getLoginUrl(userId);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(loginUrl))
                .build();
    }

    // Callback Spotify gọi về
    @GetMapping("/callback")
    public ResponseEntity<String> callback(@RequestParam String code, @RequestParam String state) {
        spotifyAuthService.handleCallback(code, state); // state chứa userId
        return ResponseEntity.ok("Spotify connected successfully");
    }
}
