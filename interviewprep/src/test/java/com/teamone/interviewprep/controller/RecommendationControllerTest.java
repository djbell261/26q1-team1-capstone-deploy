package com.teamone.interviewprep.controller;

import com.teamone.interviewprep.dto.recommendation.RecommendationResponse;
import com.teamone.interviewprep.entity.Recommendation;
import com.teamone.interviewprep.entity.User;
import com.teamone.interviewprep.exception.ResourceNotFoundException;
import com.teamone.interviewprep.exception.UnauthorizedException;
import com.teamone.interviewprep.mapper.RecommendationMapper;
import com.teamone.interviewprep.security.SecurityUtils;
import com.teamone.interviewprep.service.RecommendationService;
import com.teamone.interviewprep.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationControllerTest {

    @Mock
    private RecommendationService recommendationService;

    @Mock
    private RecommendationMapper recommendationMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private RecommendationController controller;

    private User user;
    private User otherUser;
    private Recommendation recommendation;
    private RecommendationResponse response;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setEmail("other@example.com");

        recommendation = new Recommendation();
        recommendation.setId(10L);
        recommendation.setUser(user);

        response = new RecommendationResponse();
        response.setId(10L);
    }

    @Test
    void createRecommendation_shouldReturnResponse() {
        when(recommendationService.createRecommendation(recommendation)).thenReturn(recommendation);
        when(recommendationMapper.toResponse(recommendation)).thenReturn(response);

        ResponseEntity<RecommendationResponse> result = controller.createRecommendation(recommendation);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(10L, result.getBody().getId());
    }

    @Test
    void getAllRecommendations_shouldReturnMappedList() {
        Recommendation recommendation2 = new Recommendation();
        recommendation2.setId(11L);

        RecommendationResponse response2 = new RecommendationResponse();
        response2.setId(11L);

        when(recommendationService.getAllRecommendations()).thenReturn(List.of(recommendation, recommendation2));
        when(recommendationMapper.toResponse(recommendation)).thenReturn(response);
        when(recommendationMapper.toResponse(recommendation2)).thenReturn(response2);

        ResponseEntity<List<RecommendationResponse>> result = controller.getAllRecommendations();

        assertEquals(200, result.getStatusCode().value());
        assertEquals(2, result.getBody().size());
    }

    @Test
    void generateMyRecommendations_shouldReturnMappedList() {
        Recommendation recommendation2 = new Recommendation();
        recommendation2.setId(11L);
        recommendation2.setUser(user);

        RecommendationResponse response2 = new RecommendationResponse();
        response2.setId(11L);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(recommendationService.generateRecommendationsForUser(1L)).thenReturn(List.of(recommendation, recommendation2));
            when(recommendationMapper.toResponse(recommendation)).thenReturn(response);
            when(recommendationMapper.toResponse(recommendation2)).thenReturn(response2);

            ResponseEntity<List<RecommendationResponse>> result = controller.generateMyRecommendations();

            assertEquals(200, result.getStatusCode().value());
            assertEquals(2, result.getBody().size());
        }
    }

    @Test
    void getRecommendationById_shouldReturnResponse_whenOwnedByCurrentUser() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(recommendationService.getRecommendationById(10L)).thenReturn(Optional.of(recommendation));
            when(recommendationMapper.toResponse(recommendation)).thenReturn(response);

            ResponseEntity<RecommendationResponse> result = controller.getRecommendationById(10L);

            assertEquals(200, result.getStatusCode().value());
            assertEquals(10L, result.getBody().getId());
        }
    }

    @Test
    void getRecommendationById_shouldThrowNotFound_whenMissing() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(recommendationService.getRecommendationById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> controller.getRecommendationById(99L));
        }
    }

    @Test
    void getRecommendationById_shouldThrowUnauthorized_whenNotOwner() {
        recommendation.setUser(otherUser);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(recommendationService.getRecommendationById(10L)).thenReturn(Optional.of(recommendation));

            assertThrows(UnauthorizedException.class, () -> controller.getRecommendationById(10L));
        }
    }

    @Test
    void getMyRecommendations_shouldReturnMappedList() {
        Recommendation recommendation2 = new Recommendation();
        recommendation2.setId(11L);
        recommendation2.setUser(user);

        RecommendationResponse response2 = new RecommendationResponse();
        response2.setId(11L);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(recommendationService.getRecommendationsByUserId(1L)).thenReturn(List.of(recommendation, recommendation2));
            when(recommendationMapper.toResponse(recommendation)).thenReturn(response);
            when(recommendationMapper.toResponse(recommendation2)).thenReturn(response2);

            ResponseEntity<List<RecommendationResponse>> result = controller.getMyRecommendations();

            assertEquals(200, result.getStatusCode().value());
            assertEquals(2, result.getBody().size());
        }
    }

    @Test
    void updateRecommendation_shouldReturnUpdatedResponse() {
        when(recommendationService.updateRecommendation(10L, recommendation)).thenReturn(recommendation);
        when(recommendationMapper.toResponse(recommendation)).thenReturn(response);

        ResponseEntity<RecommendationResponse> result = controller.updateRecommendation(10L, recommendation);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(10L, result.getBody().getId());
    }

    @Test
    void deleteRecommendation_shouldReturnNoContent() {
        ResponseEntity<Void> result = controller.deleteRecommendation(10L);

        assertEquals(204, result.getStatusCode().value());
        verify(recommendationService).deleteRecommendation(10L);
    }

    @Test
    void generateMyRecommendations_shouldThrowUnauthorized_whenNoAuthenticatedUser() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("");

            assertThrows(UnauthorizedException.class, () -> controller.generateMyRecommendations());
        }
    }

    @Test
    void generateMyRecommendations_shouldThrowNotFound_whenAuthenticatedUserMissingFromDatabase() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("missing@example.com");
            when(userService.getUserByEmail("missing@example.com")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> controller.generateMyRecommendations());
        }
    }
}