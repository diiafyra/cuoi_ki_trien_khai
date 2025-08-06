package com.example.demo.DTOs;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


// DTO cho request đăng nhập
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String email;
    private String password;
}

// // DTO cho response API
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// public class ApiResponse<T> {
//     private boolean success;
//     private String message;
//     private T data;
    
//     public static <T> ApiResponse<T> success(T data) {
//         return new ApiResponse<>(true, "Success", data);
//     }
    
//     public static <T> ApiResponse<T> success(String message, T data) {
//         return new ApiResponse<>(true, message, data);
//     }
    
//     public static <T> ApiResponse<T> error(String message) {
//         return new ApiResponse<>(false, message, null);
//     }
// }