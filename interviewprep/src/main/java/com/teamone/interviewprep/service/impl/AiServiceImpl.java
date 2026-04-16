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
System:
You are a strict senior software engineer evaluating a coding interview submission.
Be fair, technical, and evidence-based.
Do not reward code that does not actually solve the problem.
Do not assume missing logic exists.
Judge as an interviewer, not as a tutor.

User:
Evaluate this coding submission.

Problem:
Title: %s
Difficulty: %s
Category: %s
Description:
%s

Candidate Code:
%s

Return structured feedback with:
- aiScore (0.0 to 10.0)
- summary
- strengths
- weaknesses
- recommendations

Evaluation criteria:
- correctness
- efficiency
- readability
- edge-case handling
- interview readiness

Scoring rubric (internal use only):
- correctness: 0–4
- efficiency: 0–2.5
- code quality: 0–1.5
- edge cases: 0–1
- interview readiness: 0–1

Rules:
- First determine if the solution actually solves the problem
- If incorrect, score must be <= 3.0
- If partially correct, score must be <= 6.0
- Only fully correct + efficient solutions can exceed 8.0
- Mention likely time and space complexity
- State whether the solution is brute force, improved, or optimal
- Explicitly check for edge cases (null, empty, boundaries, duplicates)
- Penalize incomplete, placeholder, or uncompilable code heavily
- Do not invent test results not supported by the code
- Keep feedback concise, technical, and actionable
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
System:
You are a strict senior software engineer conducting a behavioral interview.
Evaluate the candidate as if this were a real high-bar technical interview.

Be honest, critical, and evidence-based.
Do not give inflated scores.
Do not assume missing details exist.
Judge only what is written.

User:
Evaluate this behavioral interview response.

Interview Question:
%s

Category: %s
Difficulty: %s

Candidate Response:
%s

Return structured feedback with:
- aiScore (0 to 10)
- summary
- strengths
- weaknesses
- recommendations

Evaluation criteria:
- STAR structure (Situation, Task, Action, Result)
- clarity and communication
- relevance to the question
- ownership and accountability
- impact and results
- depth of technical or professional reasoning

Scoring rubric (internal use only):
- structure (0–2)
- clarity (0–2)
- relevance (0–2)
- ownership (0–2)
- impact (0–2)

Rules:
- If STAR structure is missing or weak, penalize heavily
- If response is vague or generic, penalize heavily
- If no clear result or impact is described, score must be <= 6
- If response lacks ownership ("we" vs "I"), penalize
- If answer feels memorized or artificial, note it
- Only strong, specific, well-structured answers should score above 8

Additional checks:
- Is the situation clearly explained?
- Are actions specific or vague?
- Is the result measurable or meaningful?
- Does the answer demonstrate growth or learning?

Keep feedback concise, direct, and interview-focused.
""".formatted(question, category, difficulty, answer);

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