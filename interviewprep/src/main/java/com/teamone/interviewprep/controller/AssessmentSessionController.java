package com.teamone.interviewprep.controller;

import com.teamone.interviewprep.dto.session.AssessmentSessionRequest;
import com.teamone.interviewprep.dto.session.AssessmentSessionResponse;
import com.teamone.interviewprep.entity.AssessmentSession;
import com.teamone.interviewprep.entity.User;
import com.teamone.interviewprep.exception.ResourceNotFoundException;
import com.teamone.interviewprep.exception.SessionExpiredException;
import com.teamone.interviewprep.exception.UnauthorizedException;
import com.teamone.interviewprep.mapper.AssessmentSessionMapper;
import com.teamone.interviewprep.security.SecurityUtils;
import com.teamone.interviewprep.service.AssessmentSessionService;
import com.teamone.interviewprep.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AssessmentSessionController {

    private final AssessmentSessionService assessmentSessionService;
    private final UserService userService;
    private final AssessmentSessionMapper assessmentSessionMapper;

    @PostMapping
    public ResponseEntity<AssessmentSessionResponse> createSession(@RequestBody AssessmentSessionRequest request) {
        User user = getAuthenticatedUser();

        AssessmentSession session = assessmentSessionMapper.toEntity(request, user);
        AssessmentSession savedSession = assessmentSessionService.createSession(session);

        return ResponseEntity.ok(assessmentSessionMapper.toResponse(savedSession));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<AssessmentSessionResponse>> getAllSessions() {
        List<AssessmentSessionResponse> sessions = assessmentSessionService.getAllSessions()
                .stream()
                .map(assessmentSessionMapper::toResponse)
                .toList();

        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/me")
    public ResponseEntity<List<AssessmentSessionResponse>> getMySessions() {
        User user = getAuthenticatedUser();

        List<AssessmentSessionResponse> sessions = assessmentSessionService.getSessionsByUserId(user.getId())
                .stream()
                .map(assessmentSessionMapper::toResponse)
                .toList();

        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssessmentSessionResponse> getSessionById(@PathVariable Long id) {
        User user = getAuthenticatedUser();

        return assessmentSessionService.getSessionById(id)
                .map(session -> {
                    if (session.getUser() == null || !session.getUser().getId().equals(user.getId())) {
                        throw new UnauthorizedException("You are not allowed to view this session");
                    }
                    return assessmentSessionMapper.toResponse(session);
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssessmentSessionResponse> updateSession(@PathVariable Long id,
                                                                   @RequestBody AssessmentSessionRequest request) {
        User user = getAuthenticatedUser();

        AssessmentSession existing = assessmentSessionService.getSessionById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Session not found with id: " + id
                ));

        if (existing.getUser() == null || !existing.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not allowed to update this session");
        }

        if (existing.isExpired()) {
            throw new SessionExpiredException("Session has expired");
        }

        AssessmentSession updated = assessmentSessionMapper.toEntity(request, user);
        AssessmentSession saved = assessmentSessionService.updateSession(id, updated);

        return ResponseEntity.ok(assessmentSessionMapper.toResponse(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        User user = getAuthenticatedUser();

        AssessmentSession existing = assessmentSessionService.getSessionById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Session not found with id: " + id
                ));

        if (existing.getUser() == null || !existing.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not allowed to delete this session");
        }

        assessmentSessionService.deleteSession(id);
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