package com.teamone.interviewprep.dto.performance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceSummaryResponse {
    private Long userId;

    private Double averageCodingScore;
    private Double averageBehavioralScore;
    private Double overallAverageScore;

    private Integer totalCodingSubmissions;
    private Integer totalBehavioralSubmissions;

    private Map<String, Double> codingAverageByDifficulty;
    private List<RecentPerformanceItemResponse> recentCodingSubmissions;
    private List<RecentPerformanceItemResponse> recentBehavioralSubmissions;

    private List<String> weakAreas;
}