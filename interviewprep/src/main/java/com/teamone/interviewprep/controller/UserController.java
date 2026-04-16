package com.teamone.interviewprep.controller;

import com.teamone.interviewprep.dto.auth.AuthResponse;
import com.teamone.interviewprep.dto.auth.RegisterRequest;
import com.teamone.interviewprep.entity.User;
import com.teamone.interviewprep.mapper.UserMapper;
import com.teamone.interviewprep.security.SecurityUtils;
import com.teamone.interviewprep.service.AuthService;
import com.teamone.interviewprep.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    private final UserMapper userMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<AuthResponse> createUser(@RequestBody RegisterRequest request) {
        User user = userMapper.toEntity(request);
        User savedUser = authService.register(user);
        return ResponseEntity.ok(userMapper.toAuthResponse(savedUser));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<AuthResponse>> getAllUsers() {
        List<AuthResponse> users = userService.getAllUsers()
                .stream()
                .map(userMapper::toAuthResponse)
                .toList();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();

        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(userMapper.toAuthResponse(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<AuthResponse> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(userMapper::toAuthResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<AuthResponse> updateUser(@PathVariable Long id,
                                                   @RequestBody RegisterRequest request) {
        User updatedUser = userMapper.toEntity(request);
        User savedUser = userService.updateUser(id, updatedUser);
        return ResponseEntity.ok(userMapper.toAuthResponse(savedUser));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}