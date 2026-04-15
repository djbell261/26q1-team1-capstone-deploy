package com.teamone.interviewprep.service.impl;

import com.teamone.interviewprep.entity.AssessmentSession;
import com.teamone.interviewprep.exception.ResourceNotFoundException;
import com.teamone.interviewprep.repository.AssessmentSessionRepository;
import com.teamone.interviewprep.service.AssessmentSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssessmentSessionServiceImpl implements AssessmentSessionService {

    private final AssessmentSessionRepository assessmentSessionRepository;

    @Override
    public AssessmentSession createSession(AssessmentSession session) {
        return assessmentSessionRepository.save(session);
    }

    @Override
    public List<AssessmentSession> getAllSessions() {
        return assessmentSessionRepository.findAll();
    }

    @Override
    public Optional<AssessmentSession> getSessionById(Long id) {
        return assessmentSessionRepository.findById(id);
    }

    @Override
    public List<AssessmentSession> getSessionsByUserId(Long userId) {
        return assessmentSessionRepository.findByUserId(userId);
    }

    @Override
    public List<AssessmentSession> getSessionsByStatus(String status) {
        return assessmentSessionRepository.findByStatus(status);
    }

    @Override
    public AssessmentSession updateSession(Long id, AssessmentSession updatedSession) {
        return assessmentSessionRepository.findById(id)
                .map(session -> {
                    session.setType(updatedSession.getType());
                    session.setStartedAt(updatedSession.getStartedAt());
                    session.setExpiresAt(updatedSession.getExpiresAt());
                    session.setStatus(updatedSession.getStatus());
                    session.setUser(updatedSession.getUser());
                    return assessmentSessionRepository.save(session);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + id));
    }

    @Override
    public void deleteSession(Long id) {
        assessmentSessionRepository.deleteById(id);
    }
}