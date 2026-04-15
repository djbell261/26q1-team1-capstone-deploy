package com.teamone.interviewprep.dto.recommendation;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationResponse {

    private Long id;
    private String recommended;
    private String reason;
    private LocalDateTime createdAt;
    private Long userId;
}