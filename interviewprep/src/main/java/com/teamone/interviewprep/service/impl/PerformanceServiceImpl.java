package com.teamone.interviewprep.service.impl;

import com.teamone.interviewprep.dto.performance.PerformanceSummaryResponse;
import com.teamone.interviewprep.dto.performance.RecentPerformanceItemResponse;
import com.teamone.interviewprep.entity.BehavioralSubmission;
import com.teamone.interviewprep.entity.CodingSubmission;
import com.teamone.interviewprep.service.BehavioralSubmissionService;
import com.teamone.interviewprep.service.CodingSubmissionService;
import com.teamone.interviewprep.service.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PerformanceServiceImpl implements PerformanceService {

    private final CodingSubmissionService codingSubmissionService;
    private final BehavioralSubmissionService behavioralSubmissionService;

    @Override
    public PerformanceSummaryResponse getUserPerformanceSummary(Long userId) {
        List<CodingSubmission> codingSubmissions = codingSubmissionService.getSubmissionsByUserId(userId);
        List<BehavioralSubmission> behavioralSubmissions = behavioralSubmissionService.getSubmissionsByUserId(userId);

        double averageCodingScore = calculateAverageCodingScore(codingSubmissions);
        double averageBehavioralScore = calculateAverageBehavioralScore(behavioralSubmissions);
        double overallAverageScore = calculateOverallAverageScore(codingSubmissions, behavioralSubmissions);

        Map<String, Double> codingAverageByDifficulty = calculateCodingAverageByDifficulty(codingSubmissions);

        List<RecentPerformanceItemResponse> recentCoding = codingSubmissions.stream()
                .sorted(Comparator.comparing(CodingSubmission::getSubmittedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(submission -> RecentPerformanceItemResponse.builder()
                        .submissionId(submission.getId())
                        .type("CODING")
                        .title(submission.getChallenge() != null ? submission.getChallenge().getTitle() : "Unknown Challenge")
                        .difficulty(submission.getChallenge() != null ? submission.getChallenge().getDifficulty() : "Unknown")
                        .score(submission.getScore())
                        .submittedAt(submission.getSubmittedAt())
                        .build())
                .toList();

        List<RecentPerformanceItemResponse> recentBehavioral = behavioralSubmissions.stream()
                .sorted(Comparator.comparing(BehavioralSubmission::getSubmittedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(submission -> RecentPerformanceItemResponse.builder()
                        .submissionId(submission.getId())
                        .type("BEHAVIORAL")
                        .title(submission.getQuestion() != null ? submission.getQuestion().getQuestionText() : "Unknown Question")
                        .difficulty(submission.getQuestion() != null ? submission.getQuestion().getDifficulty() : "Unknown")
                        .score(submission.getScore())
                        .submittedAt(submission.getSubmittedAt())
                        .build())
                .toList();

        List<String> weakAreas = detectWeakAreas(codingSubmissions, behavioralSubmissions);

        return PerformanceSummaryResponse.builder()
                .userId(userId)
                .averageCodingScore(round(averageCodingScore))
                .averageBehavioralScore(round(averageBehavioralScore))
                .overallAverageScore(round(overallAverageScore))
                .totalCodingSubmissions(codingSubmissions.size())
                .totalBehavioralSubmissions(behavioralSubmissions.size())
                .codingAverageByDifficulty(codingAverageByDifficulty)
                .recentCodingSubmissions(recentCoding)
                .recentBehavioralSubmissions(recentBehavioral)
                .weakAreas(weakAreas)
                .build();
    }

    private double calculateAverageCodingScore(List<CodingSubmission> submissions) {
        return submissions.stream()
                .map(CodingSubmission::getScore)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    private double calculateAverageBehavioralScore(List<BehavioralSubmission> submissions) {
        return submissions.stream()
                .map(BehavioralSubmission::getScore)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    private double calculateOverallAverageScore(List<CodingSubmission> codingSubmissions,
                                                List<BehavioralSubmission> behavioralSubmissions) {
        return Stream.concat(
                        codingSubmissions.stream().map(CodingSubmission::getScore),
                        behavioralSubmissions.stream().map(BehavioralSubmission::getScore)
                )
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    private Map<String, Double> calculateCodingAverageByDifficulty(List<CodingSubmission> submissions) {
        return submissions.stream()
                .filter(submission -> submission.getScore() != null)
                .filter(submission -> submission.getChallenge() != null)
                .filter(submission -> submission.getChallenge().getDifficulty() != null)
                .collect(Collectors.groupingBy(
                        submission -> submission.getChallenge().getDifficulty().toUpperCase(),
                        LinkedHashMap::new,
                        Collectors.collectingAndThen(
                                Collectors.averagingDouble(CodingSubmission::getScore),
                                this::round
                        )
                ));
    }

    private List<String> detectWeakAreas(List<CodingSubmission> codingSubmissions,
                                         List<BehavioralSubmission> behavioralSubmissions) {
        List<String> weakAreas = new ArrayList<>();

        Map<String, Double> codingByDifficulty = calculateCodingAverageByDifficulty(codingSubmissions);
        codingByDifficulty.forEach((difficulty, avg) -> {
            if (avg < 6.0) {
                weakAreas.add("Coding difficulty weakness: " + difficulty);
            }
        });

        double behavioralAverage = calculateAverageBehavioralScore(behavioralSubmissions);
        if (!behavioralSubmissions.isEmpty() && behavioralAverage < 6.0) {
            weakAreas.add("Behavioral interview responses need improvement");
        }

        double codingAverage = calculateAverageCodingScore(codingSubmissions);
        if (!codingSubmissions.isEmpty() && codingAverage < 6.0) {
            weakAreas.add("Coding interview performance needs improvement");
        }

        if (weakAreas.isEmpty()) {
            weakAreas.add("No major weak areas detected yet");
        }

        return weakAreas;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}