package com.teamone.interviewprep.service;

import com.teamone.interviewprep.dto.performance.PerformanceSummaryResponse;

public interface PerformanceService {
    PerformanceSummaryResponse getUserPerformanceSummary(Long userId);
}