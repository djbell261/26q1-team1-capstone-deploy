package com.teamone.interviewprep.mapper;

import com.teamone.interviewprep.dto.auth.AuthResponse;
import com.teamone.interviewprep.dto.auth.RegisterRequest;
import com.teamone.interviewprep.entity.User;
import com.teamone.interviewprep.enums.UserRole;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(RegisterRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(request.getPassword())
                .role(UserRole.USER)
                .build();
    }

    public AuthResponse toAuthResponse(User user) {
        return AuthResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}