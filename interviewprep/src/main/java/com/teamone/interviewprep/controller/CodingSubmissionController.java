package com.teamone.interviewprep.controller;

import com.teamone.interviewprep.dto.coding.CodingSubmissionRequest;
import com.teamone.interviewprep.dto.coding.CodingSubmissionResponse;
import com.teamone.interviewprep.entity.AssessmentSession;
import com.teamone.interviewprep.entity.CodingChallenge;
import com.teamone.interviewprep.entity.CodingSubmission;
import com.teamone.interviewprep.entity.User;
import com.teamone.interviewprep.enums.SubmissionStatus;
import com.teamone.interviewprep.exception.ResourceNotFoundException;
import com.teamone.interviewprep.exception.SessionExpiredException;
import com.teamone.interviewprep.exception.UnauthorizedException;
import com.teamone.interviewprep.mapper.CodingSubmissionMapper;
import com.teamone.interviewprep.security.SecurityUtils;
import com.teamone.interviewprep.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/coding-submissions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class CodingSubmissionController {

    private final CodingSubmissionService codingSubmissionService;
    private final UserService userService;
    private final CodingChallengeService codingChallengeService;
    private final AssessmentSessionService assessmentSessionService;
    private final CodingSubmissionMapper codingSubmissionMapper;

    private final AiService aiService;

    @PostMapping
    public ResponseEntity<CodingSubmissionResponse> createSubmission(@RequestBody CodingSubmissionRequest request) {
        User user = getAuthenticatedUser();

        CodingChallenge challenge = codingChallengeService.getChallengeById(request.getChallengeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Challenge not found with id: " + request.getChallengeId()
                ));

        AssessmentSession session = assessmentSessionService.getSessionById(request.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Session not found with id: " + request.getSessionId()
                ));

        if (session.isExpired()) {
            throw new SessionExpiredException("Session has expired");
        }

        if (session.getUser() == null || !session.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Session does not belong to you");
        }

        CodingSubmission submission = codingSubmissionMapper.toEntity(request, user, challenge, session);
        submission.setStatus(SubmissionStatus.SUBMITTED);
        submission.setSubmittedAt(LocalDateTime.now());

        CodingSubmission savedSubmission = codingSubmissionService.createSubmission(submission);

        aiService.generateCodingFeedback(savedSubmission.getId());

        return ResponseEntity.ok(codingSubmissionMapper.toResponse(savedSubmission));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<CodingSubmissionResponse>> getAllSubmissions() {
        List<CodingSubmissionResponse> submissions = codingSubmissionService.getAllSubmissions()
                .stream()
                .map(codingSubmissionMapper::toResponse)
                .toList();

        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CodingSubmissionResponse> getSubmissionById(@PathVariable Long id) {
        User user = getAuthenticatedUser();

        CodingSubmission submission = codingSubmissionService.getSubmissionById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Submission not found with id: " + id
                ));

        if (submission.getUser() == null || !submission.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not allowed to view this submission");
        }

        return ResponseEntity.ok(codingSubmissionMapper.toResponse(submission));
    }

    @GetMapping("/me")
    public ResponseEntity<List<CodingSubmissionResponse>> getMySubmissions() {
        User user = getAuthenticatedUser();

        List<CodingSubmissionResponse> submissions = codingSubmissionService.getSubmissionsByUserId(user.getId())
                .stream()
                .map(codingSubmissionMapper::toResponse)
                .toList();

        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<CodingSubmissionResponse>> getMySubmissionsBySessionId(@PathVariable Long sessionId) {
        User user = getAuthenticatedUser();

        List<CodingSubmissionResponse> submissions = codingSubmissionService.getSubmissionsBySessionId(sessionId)
                .stream()
                .filter(submission -> submission.getUser() != null
                        && submission.getUser().getId().equals(user.getId()))
                .map(codingSubmissionMapper::toResponse)
                .toList();

        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/challenge/{challengeId}")
    public ResponseEntity<List<CodingSubmissionResponse>> getMySubmissionsByChallengeId(@PathVariable Long challengeId) {
        User user = getAuthenticatedUser();

        List<CodingSubmissionResponse> submissions = codingSubmissionService.getSubmissionsByChallengeId(challengeId)
                .stream()
                .filter(submission -> submission.getUser() != null
                        && submission.getUser().getId().equals(user.getId()))
                .map(codingSubmissionMapper::toResponse)
                .toList();

        return ResponseEntity.ok(submissions);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CodingSubmissionResponse> updateSubmission(@PathVariable Long id,
                                                                     @RequestBody CodingSubmissionRequest request) {
        User user = getAuthenticatedUser();

        CodingSubmission existingSubmission = codingSubmissionService.getSubmissionById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Submission not found with id: " + id
                ));

        if (existingSubmission.getUser() == null || !existingSubmission.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not allowed to update this submission");
        }

        CodingChallenge challenge = codingChallengeService.getChallengeById(request.getChallengeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Challenge not found with id: " + request.getChallengeId()
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

        CodingSubmission updatedSubmission = codingSubmissionMapper.toEntity(request, user, challenge, session);
        CodingSubmission savedSubmission = codingSubmissionService.updateSubmission(id, updatedSubmission);

        return ResponseEntity.ok(codingSubmissionMapper.toResponse(savedSubmission));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubmission(@PathVariable Long id) {
        User user = getAuthenticatedUser();

        CodingSubmission existingSubmission = codingSubmissionService.getSubmissionById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Submission not found with id: " + id
                ));

        if (existingSubmission.getUser() == null || !existingSubmission.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not allowed to delete this submission");
        }

        codingSubmissionService.deleteSubmission(id);
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