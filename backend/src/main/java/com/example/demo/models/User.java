package com.example.demo.models;

import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users") // Đổi tên bảng thành "users"
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private String id; // Đổi thành Long
    private String email;
    private String name;
    private String accessToken;
    private String refreshToken;
    private Timestamp expiredAt;

    @OneToMany(mappedBy = "user")
    private List<Notification> notifications;
}