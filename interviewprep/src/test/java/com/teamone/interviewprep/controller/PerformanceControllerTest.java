package com.teamone.interviewprep.controller;

import com.teamone.interviewprep.dto.performance.PerformanceSummaryResponse;
import com.teamone.interviewprep.entity.User;
import com.teamone.interviewprep.exception.ResourceNotFoundException;
import com.teamone.interviewprep.exception.UnauthorizedException;
import com.teamone.interviewprep.security.SecurityUtils;
import com.teamone.interviewprep.service.PerformanceService;
import com.teamone.interviewprep.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceControllerTest {

    @Mock
    private PerformanceService performanceService;

    @Mock
    private UserService userService;

    @InjectMocks
    private PerformanceController controller;

    @Test
    void getMyPerformance_shouldReturnSummary() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        PerformanceSummaryResponse response = new PerformanceSummaryResponse();

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(performanceService.getUserPerformanceSummary(1L)).thenReturn(response);

            ResponseEntity<PerformanceSummaryResponse> result = controller.getMyPerformance();

            assertEquals(200, result.getStatusCode().value());
            assertEquals(response, result.getBody());
        }
    }

    @Test
    void getMyPerformance_shouldThrowUnauthorized_whenNoAuthenticatedUser() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("");

            assertThrows(UnauthorizedException.class, () -> controller.getMyPerformance());
        }
    }

    @Test
    void getMyPerformance_shouldThrowNotFound_whenUserMissing() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("missing@example.com");
            when(userService.getUserByEmail("missing@example.com")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> controller.getMyPerformance());
        }
    }
}