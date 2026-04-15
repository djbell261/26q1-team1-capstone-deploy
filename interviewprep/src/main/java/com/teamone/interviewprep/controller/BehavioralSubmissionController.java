package com.teamone.interviewprep.controller;

import com.teamone.interviewprep.dto.behavioral.BehavioralSubmissionRequest;
import com.teamone.interviewprep.dto.behavioral.BehavioralSubmissionResponse;
import com.teamone.interviewprep.entity.AssessmentSession;
import com.teamone.interviewprep.entity.BehavioralQuestion;
import com.teamone.interviewprep.entity.BehavioralSubmission;
import com.teamone.interviewprep.entity.User;
import com.teamone.interviewprep.enums.SubmissionStatus;
import com.teamone.interviewprep.exception.ResourceNotFoundException;
import com.teamone.interviewprep.exception.SessionExpiredException;
import com.teamone.interviewprep.exception.UnauthorizedException;
import com.teamone.interviewprep.mapper.BehavioralSubmissionMapper;
import com.teamone.interviewprep.security.SecurityUtils;
import com.teamone.interviewprep.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/behavioral-submissions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class BehavioralSubmissionController {

    private final BehavioralSubmissionService behavioralSubmissionService;
    private final UserService userService;
    private final BehavioralQuestionService behavioralQuestionService;
    private final AssessmentSessionService assessmentSessionService;
    private final BehavioralSubmissionMapper behavioralSubmissionMapper;

    @PostMapping
    public ResponseEntity<BehavioralSubmissionResponse> createSubmission(@RequestBody BehavioralSubmissionRequest request) {
        User user = getAuthenticatedUser();

        BehavioralQuestion question = behavioralQuestionService.getQuestionById(request.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Behavioral question not found with id: " + request.getQuestionId()
                ));

        AssessmentSession session = assessmentSessionService.getSessionById(request.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Session not found with id: " + request.getSessionId()
                ));
        if (session.isExpired()) {
            throw new SessionExpiredException("Session has expired");
        }

        if (!session.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Session does not belong to you");
        }

        BehavioralSubmission submission = behavioralSubmissionMapper.toEntity(request, user, question, session);
        BehavioralSubmission savedSubmission = behavioralSubmissionService.createSubmission(submission);

        submission.setStatus(SubmissionStatus.SUBMITTED);
        submission.setSubmittedAt(LocalDateTime.now());

        return ResponseEntity.ok(behavioralSubmissionMapper.toResponse(savedSubmission));
    }

    @GetMapping("/me")
    public ResponseEntity<List<BehavioralSubmissionResponse>> getMySubmissions() {
        User user = getAuthenticatedUser();

        List<BehavioralSubmissionResponse> submissions = behavioralSubmissionService.getSubmissionsByUserId(user.getId())
                .stream()
                .map(behavioralSubmissionMapper::toResponse)
                .toList();

        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BehavioralSubmissionResponse> getSubmissionById(@PathVariable Long id) {
        User user = getAuthenticatedUser();

        BehavioralSubmission submission = behavioralSubmissionService.getSubmissionById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Submission not found with id: " + id
                ));

        if (submission.getUser() == null || !submission.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not allowed to view this submission");
        }

        return ResponseEntity.ok(behavioralSubmissionMapper.toResponse(submission));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BehavioralSubmissionResponse> updateSubmission(@PathVariable Long id,
                                                                         @RequestBody BehavioralSubmissionRequest request) {
        User user = getAuthenticatedUser();

        BehavioralSubmission existing = behavioralSubmissionService.getSubmissionById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Submission not found with id: " + id
                ));

        if (existing.getUser() == null || !existing.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not allowed to update this submission");
        }

        BehavioralQuestion question = behavioralQuestionService.getQuestionById(request.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Behavioral question not found with id: " + request.getQuestionId()
                ));

        AssessmentSession session = assessmentSessionService.getSessionById(request.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Session not found with id: " + request.getSessionId()
                ));
        if (session.isExpired()) {
            throw new SessionExpiredException("Session has expired");
        }

        if (!session.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Session does not belong to you");
        }

        BehavioralSubmission updated = behavioralSubmissionMapper.toEntity(request, user, question, session);
        BehavioralSubmission saved = behavioralSubmissionService.updateSubmission(id, updated);

        return ResponseEntity.ok(behavioralSubmissionMapper.toResponse(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubmission(@PathVariable Long id) {
        User user = getAuthenticatedUser();

        BehavioralSubmission existing = behavioralSubmissionService.getSubmissionById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Submission not found with id: " + id
                ));

        if (existing.getUser() == null || !existing.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not allowed to delete this submission");
        }

        behavioralSubmissionService.deleteSubmission(id);
        return ResponseEntity.noContent().build();
    }

    private User getAuthenticatedUser() {
        String email = SecurityUtils.getCurrentUserEmail();

        if (email == null || email.isBlank()) {
            throw new UnauthorizedException("No authenticated user found");
        }

        return userService.getUserByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with email: " + email
                ));
    }
}