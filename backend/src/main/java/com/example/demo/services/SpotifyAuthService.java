package com.example.demo.services;


import com.example.demo.config.SpotifyConfig;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SpotifyAuthService {


    private final UserRepository userRepository;
    private final SpotifyConfig config;
    private final RestTemplate restTemplate = new RestTemplate();

    public String getLoginUrl(String userId) {
        return UriComponentsBuilder.fromUriString("https://accounts.spotify.com/authorize")
                .queryParam("client_id", config.getClientId())
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", config.getRedirectUri())
                .queryParam("scope", "user-read-private playlist-read-private")
                .queryParam("state", userId) // để biết user nào đang đăng nhập
                .build().toUriString();
    }

    public void handleCallback(String code, String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + Base64.getEncoder()
                .encodeToString((config.getClientId() + ":" + config.getClientSecret()).getBytes()));

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", config.getClientSecret());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                "https://accounts.spotify.com/api/token",
                HttpMethod.POST,
                request,
                Map.class
        );

        Map<String, Object> resBody = response.getBody();
        String accessToken = (String) resBody.get("access_token");
        String refreshToken = (String) resBody.get("refresh_token");
        int expiresIn = (Integer) resBody.get("expires_in");

        Timestamp expiredAt = Timestamp.from(Instant.now().plusSeconds(expiresIn));

        // Lưu vào user
        User user = userRepository.findById(userId).orElseThrow();
        user.setAccessToken(accessToken);
        user.setRefreshToken(refreshToken);
        user.setExpiredAt(expiredAt);
        userRepository.save(user);
    }
}
