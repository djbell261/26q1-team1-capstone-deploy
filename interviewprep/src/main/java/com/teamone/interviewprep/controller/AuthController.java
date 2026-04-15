package com.teamone.interviewprep.controller;

import com.teamone.interviewprep.dto.auth.AuthResponse;
import com.teamone.interviewprep.dto.auth.LoginRequest;
import com.teamone.interviewprep.dto.auth.RegisterRequest;
import com.teamone.interviewprep.entity.User;
import com.teamone.interviewprep.mapper.UserMapper;
import com.teamone.interviewprep.security.JwtService;
import com.teamone.interviewprep.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        User user = userMapper.toEntity(request);
        User savedUser = authService.register(user);

        String token = jwtService.generateToken(savedUser.getEmail());

        AuthResponse response = userMapper.toAuthResponse(savedUser);
        response.setToken(token);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        User user = authService.login(request.getEmail(), request.getPassword());

        String token = jwtService.generateToken(user.getEmail());

        AuthResponse response = userMapper.toAuthResponse(user);
        response.setToken(token);

        return ResponseEntity.ok(response);
    }
}