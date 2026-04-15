package com.teamone.interviewprep.controller;

import com.teamone.interviewprep.dto.feedback.FeedbackResponse;
import com.teamone.interviewprep.entity.BehavioralSubmission;
import com.teamone.interviewprep.entity.CodingSubmission;
import com.teamone.interviewprep.entity.Feedback;
import com.teamone.interviewprep.entity.User;
import com.teamone.interviewprep.exception.ResourceNotFoundException;
import com.teamone.interviewprep.exception.UnauthorizedException;
import com.teamone.interviewprep.mapper.FeedbackMapper;
import com.teamone.interviewprep.security.SecurityUtils;
import com.teamone.interviewprep.service.BehavioralSubmissionService;
import com.teamone.interviewprep.service.CodingSubmissionService;
import com.teamone.interviewprep.service.FeedbackService;
import com.teamone.interviewprep.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final FeedbackMapper feedbackMapper;
    private final UserService userService;
    private final CodingSubmissionService codingSubmissionService;
    private final BehavioralSubmissionService behavioralSubmissionService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<FeedbackResponse> createFeedback(@RequestBody Feedback feedback) {
        Feedback savedFeedback = feedbackService.createFeedback(feedback);
        return ResponseEntity.ok(feedbackMapper.toResponse(savedFeedback));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<FeedbackResponse>> getAllFeedback() {
        List<FeedbackResponse> feedbackList = feedbackService.getAllFeedback()
                .stream()
                .map(feedbackMapper::toResponse)
                .toList();

        return ResponseEntity.ok(feedbackList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponse> getFeedbackById(@PathVariable Long id) {
        User user = getAuthenticatedUser();

        Feedback feedback = feedbackService.getFeedbackById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + id));

        if (!canAccessFeedback(user, feedback)) {
            throw new UnauthorizedException("You are not allowed to view this feedback");
        }

        return ResponseEntity.ok(feedbackMapper.toResponse(feedback));
    }

    @GetMapping("/coding-submission/{codingSubmissionId}")
    public ResponseEntity<FeedbackResponse> getFeedbackByCodingSubmissionId(@PathVariable Long codingSubmissionId) {
        User user = getAuthenticatedUser();

        CodingSubmission submission = codingSubmissionService.getSubmissionById(codingSubmissionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Coding submission not found with id: " + codingSubmissionId
                ));

        if (submission.getUser() == null || !submission.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not allowed to view feedback for this coding submission");
        }

        return feedbackService.getFeedbackByCodingSubmissionId(codingSubmissionId)
                .map(feedbackMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/behavioral-submission/{behavioralSubmissionId}")
    public ResponseEntity<FeedbackResponse> getFeedbackByBehavioralSubmissionId(@PathVariable Long behavioralSubmissionId) {
        User user = getAuthenticatedUser();

        BehavioralSubmission submission = behavioralSubmissionService.getSubmissionById(behavioralSubmissionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Behavioral submission not found with id: " + behavioralSubmissionId
                ));

        if (submission.getUser() == null || !submission.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not allowed to view feedback for this behavioral submission");
        }

        return feedbackService.getFeedbackByBehavioralSubmissionId(behavioralSubmissionId)
                .map(feedbackMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<FeedbackResponse> updateFeedback(@PathVariable Long id,
                                                           @RequestBody Feedback updatedFeedback) {
        Feedback savedFeedback = feedbackService.updateFeedback(id, updatedFeedback);
        return ResponseEntity.ok(feedbackMapper.toResponse(savedFeedback));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
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

    private boolean canAccessFeedback(User user, Feedback feedback) {
        if (feedback.getCodingSubmission() != null &&
                feedback.getCodingSubmission().getUser() != null &&
                feedback.getCodingSubmission().getUser().getId().equals(user.getId())) {
            return true;
        }

        if (feedback.getBehavioralSubmission() != null &&
                feedback.getBehavioralSubmission().getUser() != null &&
                feedback.getBehavioralSubmission().getUser().getId().equals(user.getId())) {
            return true;
        }

        return false;
    }
}