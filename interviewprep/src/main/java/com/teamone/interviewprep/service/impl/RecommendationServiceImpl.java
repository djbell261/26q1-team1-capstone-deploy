package com.teamone.interviewprep.service.impl;

import com.teamone.interviewprep.dto.performance.PerformanceSummaryResponse;
import com.teamone.interviewprep.entity.Feedback;
import com.teamone.interviewprep.entity.Recommendation;
import com.teamone.interviewprep.entity.User;
import com.teamone.interviewprep.enums.WeaknessCategory;
import com.teamone.interviewprep.exception.ResourceNotFoundException;
import com.teamone.interviewprep.repository.RecommendationRepository;
import com.teamone.interviewprep.service.FeedbackService;
import com.teamone.interviewprep.service.PerformanceService;
import com.teamone.interviewprep.service.RecommendationService;
import com.teamone.interviewprep.service.UserService;
import com.teamone.interviewprep.util.FeedbackWeaknessParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final UserService userService;
    private final PerformanceService performanceService;
    private final FeedbackService feedbackService;
    private final FeedbackWeaknessParser feedbackWeaknessParser;

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

    @Override
    public List<Recommendation> generateRecommendationsForUser(Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        PerformanceSummaryResponse performance = performanceService.getUserPerformanceSummary(userId);

        List<Recommendation> existing = recommendationRepository.findByUserId(userId);
        if (!existing.isEmpty()) {
            recommendationRepository.deleteAll(existing);
        }

        List<Recommendation> generated = new ArrayList<>();
        Set<String> seenKeys = new LinkedHashSet<>();

        if (performance.getTotalCodingSubmissions() == 0 && performance.getTotalBehavioralSubmissions() == 0) {
            addRecommendation(
                    generated,
                    seenKeys,
                    buildRecommendation(
                            user,
                            "Complete your first coding and behavioral submissions",
                            "No submission history exists yet, so the platform cannot detect performance patterns or weak areas."
                    )
            );
        }

        if (performance.getAverageCodingScore() != null
                && performance.getTotalCodingSubmissions() > 0
                && performance.getAverageCodingScore() < 6.0) {
            addRecommendation(
                    generated,
                    seenKeys,
                    buildRecommendation(
                            user,
                            "Practice more coding problems under timed conditions",
                            "Your average coding score is below 6.0, which suggests correctness, efficiency, or edge-case handling needs improvement."
                    )
            );
        }

        if (performance.getAverageBehavioralScore() != null
                && performance.getTotalBehavioralSubmissions() > 0
                && performance.getAverageBehavioralScore() < 6.0) {
            addRecommendation(
                    generated,
                    seenKeys,
                    buildRecommendation(
                            user,
                            "Improve STAR structure in behavioral responses",
                            "Your behavioral performance is below 6.0, which suggests issues with clarity, ownership, structure, or measurable impact."
                    )
            );
        }

        if (performance.getCodingAverageByDifficulty() != null) {
            performance.getCodingAverageByDifficulty().forEach((difficulty, score) -> {
                if (score != null && score < 6.0) {
                    addRecommendation(
                            generated,
                            seenKeys,
                            buildRecommendation(
                                    user,
                                    "Focus on " + difficulty + " coding challenges",
                                    "Your average score for " + difficulty + " coding problems is below 6.0, indicating this difficulty level is currently a weak area."
                            )
                    );
                }
            });
        }

        List<Feedback> allFeedback = feedbackService.getAllFeedback().stream()
                .filter(feedback -> {
                    if (feedback.getCodingSubmission() != null
                            && feedback.getCodingSubmission().getUser() != null
                            && feedback.getCodingSubmission().getUser().getId().equals(userId)) {
                        return true;
                    }

                    return feedback.getBehavioralSubmission() != null
                            && feedback.getBehavioralSubmission().getUser() != null
                            && feedback.getBehavioralSubmission().getUser().getId().equals(userId);
                })
                .toList();

        Set<WeaknessCategory> parsedCategories = new LinkedHashSet<>();
        for (Feedback feedback : allFeedback) {
            parsedCategories.addAll(feedbackWeaknessParser.parseCategories(feedback.getWeaknesses()));
        }

        for (WeaknessCategory category : parsedCategories) {
            switch (category) {
                case TIME_COMPLEXITY -> addRecommendation(
                        generated,
                        seenKeys,
                        buildRecommendation(
                                user,
                                "Practice optimizing time complexity",
                                "Your feedback suggests inefficient solutions or brute-force approaches. Focus on patterns like hash maps, two pointers, and sliding window."
                        )
                );

                case SPACE_COMPLEXITY -> addRecommendation(
                        generated,
                        seenKeys,
                        buildRecommendation(
                                user,
                                "Review space complexity tradeoffs",
                                "Your feedback suggests inefficient memory usage. Practice reducing extra space and comparing in-place vs auxiliary data structure solutions."
                        )
                );

                case EDGE_CASES, INPUT_VALIDATION -> addRecommendation(
                        generated,
                        seenKeys,
                        buildRecommendation(
                                user,
                                "Practice handling edge cases explicitly",
                                "Your feedback suggests missed null, empty, boundary, duplicate, or invalid-input scenarios. Add edge-case checks before finalizing submissions."
                        )
                );

                case CORRECTNESS -> addRecommendation(
                        generated,
                        seenKeys,
                        buildRecommendation(
                                user,
                                "Focus on correctness before optimization",
                                "Your feedback suggests some submissions may be incomplete or incorrect. Slow down and validate logic against the core problem requirements first."
                        )
                );

                case READABILITY, CODE_STYLE -> addRecommendation(
                        generated,
                        seenKeys,
                        buildRecommendation(
                                user,
                                "Improve code readability and formatting",
                                "Your feedback suggests readability issues. Use cleaner formatting, naming, spacing, and structure so your solution is easier to review in interviews."
                        )
                );

                case STAR_STRUCTURE -> addRecommendation(
                        generated,
                        seenKeys,
                        buildRecommendation(
                                user,
                                "Strengthen STAR structure in behavioral answers",
                                "Your feedback suggests your responses need clearer Situation, Task, Action, and Result structure."
                        )
                );

                case CLARITY -> addRecommendation(
                        generated,
                        seenKeys,
                        buildRecommendation(
                                user,
                                "Make responses more specific and clear",
                                "Your feedback suggests vague or generic explanations. Use direct wording, concrete actions, and clearer examples."
                        )
                );

                case OWNERSHIP -> addRecommendation(
                        generated,
                        seenKeys,
                        buildRecommendation(
                                user,
                                "Emphasize your individual ownership",
                                "Your feedback suggests you should highlight what you specifically did, not just what the team did."
                        )
                );

                case IMPACT -> addRecommendation(
                        generated,
                        seenKeys,
                        buildRecommendation(
                                user,
                                "Highlight measurable impact in behavioral answers",
                                "Your feedback suggests weak or missing results. Add measurable outcomes, improvements, or lessons learned."
                        )
                );

                case RELEVANCE -> addRecommendation(
                        generated,
                        seenKeys,
                        buildRecommendation(
                                user,
                                "Keep responses tightly aligned to the prompt",
                                "Your feedback suggests some answers may drift away from the actual interview question. Stay focused on relevance."
                        )
                );

                case GENERAL -> {
                    // no-op
                }
            }
        }

        if (performance.getWeakAreas() != null) {
            for (String weakArea : performance.getWeakAreas()) {
                if (weakArea == null || weakArea.isBlank()
                        || weakArea.equalsIgnoreCase("No major weak areas detected yet")) {
                    continue;
                }

                String normalizedWeakArea = weakArea.trim().toLowerCase();

                if (normalizedWeakArea.startsWith("coding difficulty weakness:")) {
                    continue;
                }

                if (normalizedWeakArea.contains("coding interview performance needs improvement")) {
                    continue;
                }

                if (normalizedWeakArea.contains("behavioral interview responses need improvement")) {
                    continue;
                }

                addRecommendation(
                        generated,
                        seenKeys,
                        buildRecommendation(
                                user,
                                "Target weak area: " + weakArea,
                                "This weak area was detected from your recent performance summary and should be prioritized in future practice."
                        )
                );
            }
        }

        if (generated.isEmpty()) {
            addRecommendation(
                    generated,
                    seenKeys,
                    buildRecommendation(
                            user,
                            "Maintain consistent interview practice",
                            "Your current performance does not show any major weak areas, so the best next step is to continue practicing consistently and increase difficulty gradually."
                    )
            );
        }

        return recommendationRepository.saveAll(generated);
    }

    private Recommendation buildRecommendation(User user, String recommended, String reason) {
        return Recommendation.builder()
                .recommended(recommended)
                .reason(reason)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();
    }

    private void addRecommendation(List<Recommendation> generated,
                                   Set<String> seenKeys,
                                   Recommendation recommendation) {
        String key = normalizeRecommendationKey(recommendation.getRecommended());

        if (seenKeys.add(key)) {
            generated.add(recommendation);
        }
    }

    private String normalizeRecommendationKey(String text) {
        if (text == null) {
            return "";
        }

        String normalized = text.trim().toLowerCase();

        if (normalized.startsWith("target weak area: ")) {
            normalized = normalized.replace("target weak area: ", "");
        }

        if (normalized.startsWith("coding difficulty weakness: ")) {
            normalized = normalized.replace("coding difficulty weakness: ", "focus on ");
            normalized = normalized + " coding challenges";
        }

        return normalized;
    }
}