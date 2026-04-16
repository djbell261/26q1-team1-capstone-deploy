package com.teamone.interviewprep.controller;

import com.teamone.interviewprep.dto.auth.AuthResponse;
import com.teamone.interviewprep.dto.auth.LoginRequest;
import com.teamone.interviewprep.dto.auth.RegisterRequest;
import com.teamone.interviewprep.entity.User;
import com.teamone.interviewprep.mapper.UserMapper;
import com.teamone.interviewprep.security.JwtService;
import com.teamone.interviewprep.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthController authController;

    @Test
    void login_shouldReturnAuthResponseWithToken() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("secret");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        AuthResponse response = new AuthResponse();
        response.setId(1L);
        response.setEmail("test@example.com");

        when(authService.login("test@example.com", "secret")).thenReturn(user);
        when(jwtService.generateToken("test@example.com")).thenReturn("fake-jwt");
        when(userMapper.toAuthResponse(user)).thenReturn(response);

        ResponseEntity<AuthResponse> result = authController.login(request);

        assertEquals(200, result.getStatusCode().value());
        assertEquals("test@example.com", result.getBody().getEmail());
        assertEquals("fake-jwt", result.getBody().getToken());
    }

    @Test
    void register_shouldReturnAuthResponseWithToken() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@example.com");
        request.setPassword("secret");

        User user = new User();
        user.setEmail("new@example.com");

        User saved = new User();
        saved.setId(2L);
        saved.setEmail("new@example.com");

        AuthResponse response = new AuthResponse();
        response.setId(2L);
        response.setEmail("new@example.com");

        when(userMapper.toEntity(request)).thenReturn(user);
        when(authService.register(user)).thenReturn(saved);
        when(jwtService.generateToken("new@example.com")).thenReturn("jwt-token");
        when(userMapper.toAuthResponse(saved)).thenReturn(response);

        ResponseEntity<AuthResponse> result = authController.register(request);

        assertEquals(200, result.getStatusCode().value());
        assertEquals("new@example.com", result.getBody().getEmail());
        assertEquals("jwt-token", result.getBody().getToken());
    }
}