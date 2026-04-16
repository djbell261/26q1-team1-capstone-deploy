package com.teamone.interviewprep.dto.performance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentPerformanceItemResponse {
    private Long submissionId;
    private String type;
    private String title;
    private String difficulty;
    private Double score;
    private LocalDateTime submittedAt;
}