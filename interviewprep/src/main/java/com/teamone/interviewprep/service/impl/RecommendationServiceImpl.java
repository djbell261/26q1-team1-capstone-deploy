package com.teamone.interviewprep.service.impl;

import com.teamone.interviewprep.entity.Recommendation;
import com.teamone.interviewprep.exception.ResourceNotFoundException;
import com.teamone.interviewprep.repository.RecommendationRepository;
import com.teamone.interviewprep.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;

    @Override
    public Recommendation createRecommendation(Recommendation recommendation) {
        return recommendationRepository.save(recommendation);
    }

    @Override
    public List<Recommendation> getAllRecommendations() {
        return recommendationRepository.findAll();
    }

    @Override
    public Optional<Recommendation> getRecommendationById(Long id) {
        return recommendationRepository.findById(id);
    }

    @Override
    public List<Recommendation> getRecommendationsByUserId(Long userId) {
        return recommendationRepository.findByUserId(userId);
    }

    @Override
    public Recommendation updateRecommendation(Long id, Recommendation updatedRecommendation) {
        return recommendationRepository.findById(id)
                .map(recommendation -> {
                    recommendation.setRecommended(updatedRecommendation.getRecommended());
                    recommendation.setReason(updatedRecommendation.getReason());
                    recommendation.setCreatedAt(updatedRecommendation.getCreatedAt());
                    recommendation.setUser(updatedRecommendation.getUser());
                    return recommendationRepository.save(recommendation);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation not found with id: " + id));
    }

    @Override
    public void deleteRecommendation(Long id) {
        recommendationRepository.deleteById(id);
    }
}