package com.teamone.interviewprep.service.impl;

import com.openai.client.OpenAIClient;
import com.openai.models.responses.ResponseCreateParams;
import com.teamone.interviewprep.dto.ai.AiFeedbackResult;
import com.teamone.interviewprep.entity.BehavioralSubmission;
import com.teamone.interviewprep.entity.CodingSubmission;
import com.teamone.interviewprep.entity.Feedback;
import com.teamone.interviewprep.enums.FeedbackStatus;
import com.teamone.interviewprep.enums.FeedbackType;
import com.teamone.interviewprep.enums.SubmissionStatus;
import com.teamone.interviewprep.exception.ResourceNotFoundException;
import com.teamone.interviewprep.service.AiService;
import com.teamone.interviewprep.service.BehavioralSubmissionService;
import com.teamone.interviewprep.service.CodingSubmissionService;
import com.teamone.interviewprep.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final OpenAIClient openAIClient;
    private final CodingSubmissionService codingSubmissionService;
    private final BehavioralSubmissionService behavioralSubmissionService;
    private final FeedbackService feedbackService;

    @Value("${openai.model}")
    private String model;

    @Override
    public Feedback generateCodingFeedback(Long codingSubmissionId) {
        CodingSubmission submission = codingSubmissionService.getSubmissionById(codingSubmissionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Coding submission not found with id: " + codingSubmissionId
                ));

        if (submission.getFeedback() != null) {
            return submission.getFeedback();
        }

        try {
            AiFeedbackResult result = evaluateCodingSubmission(submission);

            Feedback feedback = Feedback.builder()
                    .type(FeedbackType.CODING)
                    .aiScore(result.getAiScore())
                    .summary(result.getSummary())
                    .strengths(result.getStrengths())
                    .weaknesses(result.getWeaknesses())
                    .recommendations(result.getRecommendations())
                    .status(FeedbackStatus.GENERATED)
                    .generatedAt(LocalDateTime.now())
                    .codingSubmission(submission)
                    .build();

            submission.setScore(result.getAiScore());
            submission.setStatus(SubmissionStatus.GRADED);

            codingSubmissionService.updateSubmission(submission.getId(), submission);

            return feedbackService.createFeedback(feedback);

        } catch (Exception e) {
            Feedback failedFeedback = Feedback.builder()
                    .type(FeedbackType.CODING)
                    .status(FeedbackStatus.FAILED)
                    .summary("AI feedback generation failed: " + e.getMessage())
                    .generatedAt(LocalDateTime.now())
                    .codingSubmission(submission)
                    .build();

            return feedbackService.createFeedback(failedFeedback);
        }
    }

    @Override
    public Feedback generateBehavioralFeedback(Long behavioralSubmissionId) {
        BehavioralSubmission submission = behavioralSubmissionService.getSubmissionById(behavioralSubmissionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Behavioral submission not found with id: " + behavioralSubmissionId
                ));

        if (submission.getFeedback() != null) {
            return submission.getFeedback();
        }

        try {
            AiFeedbackResult result = evaluateBehavioralSubmission(submission);

            Feedback feedback = Feedback.builder()
                    .type(FeedbackType.BEHAVIORAL)
                    .aiScore(result.getAiScore())
                    .summary(result.getSummary())
                    .strengths(result.getStrengths())
                    .weaknesses(result.getWeaknesses())
                    .recommendations(result.getRecommendations())
                    .status(FeedbackStatus.GENERATED)
                    .generatedAt(LocalDateTime.now())
                    .behavioralSubmission(submission)
                    .build();

            submission.setScore(result.getAiScore());
            submission.setStatus(SubmissionStatus.GRADED);

            behavioralSubmissionService.updateSubmission(submission.getId(), submission);

            return feedbackService.createFeedback(feedback);

        } catch (Exception e) {
            Feedback failedFeedback = Feedback.builder()
                    .type(FeedbackType.BEHAVIORAL)
                    .status(FeedbackStatus.FAILED)
                    .summary("AI feedback generation failed: " + e.getMessage())
                    .generatedAt(LocalDateTime.now())
                    .behavioralSubmission(submission)
                    .build();

            return feedbackService.createFeedback(failedFeedback);
        }
    }

    private AiFeedbackResult evaluateCodingSubmission(CodingSubmission submission) {
        String title = submission.getChallenge() != null ? submission.getChallenge().getTitle() : "Unknown";
        String description = submission.getChallenge() != null ? submission.getChallenge().getDescription() : "";
        String difficulty = submission.getChallenge() != null ? submission.getChallenge().getDifficulty() : "Unknown";
        String category = submission.getChallenge() != null ? submission.getChallenge().getCategory() : "Unknown";
        String code = submission.getCode() != null ? submission.getCode() : "";

        String prompt = """
            You are evaluating a candidate's coding interview submission.

            Return structured feedback with:
            - aiScore: a number from 0 to 100
            - summary: concise overall evaluation
            - strengths: strongest parts of the submission
            - weaknesses: biggest problems in correctness, efficiency, readability, or edge cases
            - recommendations: actionable next steps

            Evaluate based on:
            - correctness
            - efficiency
            - readability and style
            - edge-case handling
            - likely bugs

            Challenge Title: %s
            Difficulty: %s
            Category: %s

            Challenge Description:
            %s

            Candidate Code:
            %s
            """.formatted(title, difficulty, category, description, code);

        var response = openAIClient.responses().create(
                ResponseCreateParams.builder()
                        .model(model)
                        .input(prompt)
                        .text(AiFeedbackResult.class)
                        .build()
        );

        return response.output()
                .stream()
                .flatMap(item -> item.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No structured coding feedback returned"));
    }

    private AiFeedbackResult evaluateBehavioralSubmission(BehavioralSubmission submission) {
        String question = submission.getQuestion() != null ? submission.getQuestion().getQuestionText() : "";
        String category = submission.getQuestion() != null ? submission.getQuestion().getCategory() : "Unknown";
        String difficulty = submission.getQuestion() != null ? submission.getQuestion().getDifficulty() : "Unknown";
        String answer = submission.getResponseText() != null ? submission.getResponseText() : "";

        String prompt = """
            You are evaluating a candidate's behavioral interview response.

            Return structured feedback with:
            - aiScore: a number from 0 to 100
            - summary: concise overall evaluation
            - strengths: strongest parts of the response
            - weaknesses: biggest problems in STAR structure, clarity, relevance, ownership, or impact
            - recommendations: actionable next steps

            Evaluate based on:
            - STAR structure
            - clarity
            - relevance
            - ownership
            - quality of result or impact

            Question Category: %s
            Difficulty: %s

            Interview Question:
            %s

            Candidate Response:
            %s
            """.formatted(category, difficulty, question, answer);

        var response = openAIClient.responses().create(
                ResponseCreateParams.builder()
                        .model(model)
                        .input(prompt)
                        .text(AiFeedbackResult.class)
                        .build()
        );

        return response.output()
                .stream()
                .flatMap(item -> item.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No structured behavioral feedback returned"));
    }
}