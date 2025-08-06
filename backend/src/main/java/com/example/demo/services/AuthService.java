package com.example.demo.services;

import com.example.demo.DTOs.*;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private FirebaseAuth firebaseAuth;

    @Autowired
    private UserRepository userRepository;

    @Value("${firebase.api-key}")
    private String firebaseApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public AuthResponse register(RegisterRequest request) throws Exception {
        try {
            // Kiểm tra email đã tồn tại
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already exists");
            }

            // Tạo user trên Firebase
            CreateRequest createRequest = new CreateRequest()
                    .setEmail(request.getEmail())
                    .setPassword(request.getPassword())
                    .setDisplayName(request.getName());

            UserRecord userRecord = firebaseAuth.createUser(createRequest);

            // Đăng nhập để lấy token
            AuthResponse authResponse = signInWithEmailAndPassword(request.getEmail(), request.getPassword());

            // Lưu user vào database
            User user = new User();
            user.setId(userRecord.getUid());
            user.setEmail(request.getEmail());
            user.setName(request.getName());

            userRepository.save(user);

            return authResponse;

        } catch (FirebaseAuthException e) {
            throw new RuntimeException("Firebase registration failed: " + e.getMessage());
        }
    }

    public AuthResponse login(LoginRequest request) throws Exception {
        try {
            // Đăng nhập với Firebase
            AuthResponse authResponse = signInWithEmailAndPassword(request.getEmail(), request.getPassword());

            return authResponse;

        } catch (FirebaseAuthException e) {
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }

    public AuthResponse loginWithGoogle(String idToken) throws FirebaseAuthException {
        System.out.println("🧪 Bắt đầu verify idToken...");

        FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
        System.out.println("✅ Token hợp lệ. UID: " + decodedToken.getUid());

        String uid = decodedToken.getUid();
        String email = decodedToken.getEmail();
        String name = decodedToken.getName();

        System.out.println("📧 Email: " + email + ", 👤 Tên: " + name);

        // Kiểm tra user có tồn tại chưa
        Optional<User> optionalUser = userRepository.findById(uid);
        if (optionalUser.isEmpty()) {
            System.out.println("👤 User chưa tồn tại. Tiến hành tạo mới...");

            User newUser = new User();
            newUser.setId(uid);
            newUser.setEmail(email);
            newUser.setName(name);
            userRepository.save(newUser);

            System.out.println("✅ Tạo user mới thành công");
        } else {
            System.out.println("👤 User đã tồn tại trong hệ thống");
        }

        return new AuthResponse(uid, email, name);
    }

    public void logout(String uid) {
        Optional<User> userOptional = userRepository.findById(uid);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setAccessToken(null);
            user.setRefreshToken(null);
            user.setExpiredAt(null);
            userRepository.save(user);
        }
    }

    private AuthResponse signInWithEmailAndPassword(String email, String password) throws Exception {
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + firebaseApiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        body.put("returnSecureToken", true);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            String idToken = (String) responseBody.get("idToken");

            // Verify token để lấy thông tin user
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);

            return new AuthResponse(
                    decodedToken.getUid(),
                    decodedToken.getEmail(),
                    decodedToken.getName());
        } else {
            throw new RuntimeException("Authentication failed");
        }
    }

    public User getUserByToken(String token) throws FirebaseAuthException {
        FirebaseToken decodedToken = firebaseAuth.verifyIdToken(token);
        String uid = decodedToken.getUid();
        return userRepository.findById(uid).orElse(null);
    }
}