package com.teamone.interviewprep.controller;

import com.teamone.interviewprep.dto.performance.PerformanceSummaryResponse;
import com.teamone.interviewprep.entity.User;
import com.teamone.interviewprep.exception.ResourceNotFoundException;
import com.teamone.interviewprep.exception.UnauthorizedException;
import com.teamone.interviewprep.security.SecurityUtils;
import com.teamone.interviewprep.service.PerformanceService;
import com.teamone.interviewprep.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/performance")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class PerformanceController {

    private final PerformanceService performanceService;
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<PerformanceSummaryResponse> getMyPerformance() {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(performanceService.getUserPerformanceSummary(user.getId()));
    }

    private User getAuthenticatedUser() {
        String email = SecurityUtils.getCurrentUserEmail();

        if (email == null || email.isBlank()) {
            throw new UnauthorizedException("No authenticated user found");
        }

        return userService.getUserByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with email: " + email
                ));
    }
}