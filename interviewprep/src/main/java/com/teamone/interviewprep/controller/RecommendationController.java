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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final RecommendationMapper recommendationMapper;
    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<RecommendationResponse> createRecommendation(@RequestBody Recommendation recommendation) {
        Recommendation savedRecommendation = recommendationService.createRecommendation(recommendation);
        return ResponseEntity.ok(recommendationMapper.toResponse(savedRecommendation));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<RecommendationResponse>> getAllRecommendations() {
        List<RecommendationResponse> recommendations = recommendationService.getAllRecommendations()
                .stream()
                .map(recommendationMapper::toResponse)
                .toList();

        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecommendationResponse> getRecommendationById(@PathVariable Long id) {
        User user = getAuthenticatedUser();

        Recommendation recommendation = recommendationService.getRecommendationById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Recommendation not found with id: " + id
                ));

        if (recommendation.getUser() == null || !recommendation.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not allowed to view this recommendation");
        }

        return ResponseEntity.ok(recommendationMapper.toResponse(recommendation));
    }

    @GetMapping("/me")
    public ResponseEntity<List<RecommendationResponse>> getMyRecommendations() {
        User user = getAuthenticatedUser();

        List<RecommendationResponse> recommendations = recommendationService.getRecommendationsByUserId(user.getId())
                .stream()
                .map(recommendationMapper::toResponse)
                .toList();

        return ResponseEntity.ok(recommendations);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<RecommendationResponse> updateRecommendation(@PathVariable Long id,
                                                                       @RequestBody Recommendation updatedRecommendation) {
        Recommendation savedRecommendation = recommendationService.updateRecommendation(id, updatedRecommendation);
        return ResponseEntity.ok(recommendationMapper.toResponse(savedRecommendation));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecommendation(@PathVariable Long id) {
        recommendationService.deleteRecommendation(id);
        return ResponseEntity.noContent().build();
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