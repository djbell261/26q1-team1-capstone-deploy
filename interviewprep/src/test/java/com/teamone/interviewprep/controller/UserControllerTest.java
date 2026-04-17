package com.teamone.interviewprep.controller;

import com.teamone.interviewprep.dto.auth.AuthResponse;
import com.teamone.interviewprep.dto.auth.RegisterRequest;
import com.teamone.interviewprep.entity.User;
import com.teamone.interviewprep.mapper.UserMapper;
import com.teamone.interviewprep.security.SecurityUtils;
import com.teamone.interviewprep.service.AuthService;
import com.teamone.interviewprep.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock private UserService userService;
    @Mock private AuthService authService;
    @Mock private UserMapper userMapper;

    @InjectMocks
    private UserController controller;

    @Test
    void createUser_shouldReturnAuthResponse() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@example.com");
        request.setPassword("secret");

        User mapped = new User();
        mapped.setEmail("new@example.com");

        User saved = new User();
        saved.setId(1L);
        saved.setEmail("new@example.com");

        AuthResponse response = new AuthResponse();
        response.setId(1L);
        response.setEmail("new@example.com");

        when(userMapper.toEntity(request)).thenReturn(mapped);
        when(authService.register(mapped)).thenReturn(saved);
        when(userMapper.toAuthResponse(saved)).thenReturn(response);

        ResponseEntity<AuthResponse> result = controller.createUser(request);

        assertEquals(200, result.getStatusCode().value());
        assertEquals("new@example.com", result.getBody().getEmail());
    }

    @Test
    void getAllUsers_shouldReturnMappedList() {
        User user = new User();
        user.setId(1L);

        AuthResponse response = new AuthResponse();
        response.setId(1L);

        when(userService.getAllUsers()).thenReturn(List.of(user));
        when(userMapper.toAuthResponse(user)).thenReturn(response);

        ResponseEntity<List<AuthResponse>> result = controller.getAllUsers();

        assertEquals(200, result.getStatusCode().value());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void getCurrentUser_shouldReturnAuthResponse() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        AuthResponse response = new AuthResponse();
        response.setId(1L);
        response.setEmail("test@example.com");

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(userMapper.toAuthResponse(user)).thenReturn(response);

            ResponseEntity<AuthResponse> result = controller.getCurrentUser();

            assertEquals(200, result.getStatusCode().value());
            assertEquals("test@example.com", result.getBody().getEmail());
        }
    }
}