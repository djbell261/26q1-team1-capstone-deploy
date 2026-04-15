package com.teamone.interviewprep.service;

import com.teamone.interviewprep.entity.AssessmentSession;

import java.util.List;
import java.util.Optional;

public interface AssessmentSessionService {
    AssessmentSession createSession(AssessmentSession session);
    List<AssessmentSession> getAllSessions();
    Optional<AssessmentSession> getSessionById(Long id);
    List<AssessmentSession> getSessionsByUserId(Long userId);
    List<AssessmentSession> getSessionsByStatus(String status);
    AssessmentSession updateSession(Long id, AssessmentSession updatedSession);
    void deleteSession(Long id);
}