package com.hustleflow.authentication.controller;

import org.springframework.http.ResponseEntity; // Import các DTO từ thư mục con
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hustleflow.authentication.dto.LoginRequest;
import com.hustleflow.authentication.dto.LoginResponse;
import com.hustleflow.authentication.dto.RegisterRequest;
import com.hustleflow.authentication.dto.RegisterResponse;
import com.hustleflow.authentication.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor // Tự động inject AuthService vào
public class AuthController {

    private final AuthService authService;

    // API Đăng ký: POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    // API Đăng nhập: POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}