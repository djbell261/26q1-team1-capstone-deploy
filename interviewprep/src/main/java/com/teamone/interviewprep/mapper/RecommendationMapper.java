package com.teamone.interviewprep.mapper;

import com.teamone.interviewprep.dto.recommendation.RecommendationResponse;
import com.teamone.interviewprep.entity.Recommendation;
import org.springframework.stereotype.Component;

@Component
public class RecommendationMapper {

    public RecommendationResponse toResponse(Recommendation recommendation) {
        return RecommendationResponse.builder()
                .id(recommendation.getId())
                .recommended(recommendation.getRecommended())
                .reason(recommendation.getReason())
                .createdAt(recommendation.getCreatedAt())
                .userId(recommendation.getUser() != null ? recommendation.getUser().getId() : null)
                .build();
    }
}