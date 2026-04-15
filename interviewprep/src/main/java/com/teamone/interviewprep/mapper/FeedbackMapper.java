package com.teamone.interviewprep.mapper;

import com.teamone.interviewprep.dto.feedback.FeedbackResponse;
import com.teamone.interviewprep.entity.Feedback;
import org.springframework.stereotype.Component;

@Component
public class FeedbackMapper {

    public FeedbackResponse toResponse(Feedback feedback) {
        return FeedbackResponse.builder()
                .id(feedback.getId())
                .type(feedback.getType().name())
                .aiScore(feedback.getAiScore())
                .summary(feedback.getSummary())
                .strengths(feedback.getStrengths())
                .weaknesses(feedback.getWeaknesses())
                .recommendations(feedback.getRecommendations())
                .status(feedback.getStatus().name())
                .generatedAt(feedback.getGeneratedAt())
                .codingSubmissionId(
                        feedback.getCodingSubmission() != null
                                ? feedback.getCodingSubmission().getId()
                                : null
                )
                .behavioralSubmissionId(
                        feedback.getBehavioralSubmission() != null
                                ? feedback.getBehavioralSubmission().getId()
                                : null
                )
                .build();
    }
}