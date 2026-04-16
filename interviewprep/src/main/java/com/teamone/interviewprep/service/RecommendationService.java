package com.teamone.interviewprep.service;

import com.teamone.interviewprep.entity.Recommendation;

import java.util.List;
import java.util.Optional;

public interface RecommendationService {
    Recommendation createRecommendation(Recommendation recommendation);
    List<Recommendation> getAllRecommendations();
    Optional<Recommendation> getRecommendationById(Long id);
    List<Recommendation> getRecommendationsByUserId(Long userId);
    Recommendation updateRecommendation(Long id, Recommendation updatedRecommendation);
    void deleteRecommendation(Long id);

    List<Recommendation> generateRecommendationsForUser(Long userId);
}