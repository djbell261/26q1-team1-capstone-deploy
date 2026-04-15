package com.teamone.interviewprep.service.impl;

import com.teamone.interviewprep.entity.CodingSubmission;
import com.teamone.interviewprep.exception.ResourceNotFoundException;
import com.teamone.interviewprep.repository.CodingSubmissionRepository;
import com.teamone.interviewprep.service.CodingSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CodingSubmissionServiceImpl implements CodingSubmissionService {

    private final CodingSubmissionRepository codingSubmissionRepository;

    @Override
    public CodingSubmission createSubmission(CodingSubmission submission) {
        return codingSubmissionRepository.save(submission);
    }

    @Override
    public List<CodingSubmission> getAllSubmissions() {
        return codingSubmissionRepository.findAll();
    }

    @Override
    public Optional<CodingSubmission> getSubmissionById(Long id) {
        return codingSubmissionRepository.findById(id);
    }

    @Override
    public List<CodingSubmission> getSubmissionsByUserId(Long userId) {
        return codingSubmissionRepository.findByUserId(userId);
    }

    @Override
    public List<CodingSubmission> getSubmissionsBySessionId(Long sessionId) {
        return codingSubmissionRepository.findBySessionId(sessionId);
    }

    @Override
    public List<CodingSubmission> getSubmissionsByChallengeId(Long challengeId) {
        return codingSubmissionRepository.findByChallengeId(challengeId);
    }

    @Override
    public CodingSubmission updateSubmission(Long id, CodingSubmission updatedSubmission) {
        return codingSubmissionRepository.findById(id)
                .map(submission -> {
                    submission.setCode(updatedSubmission.getCode());
                    submission.setScore(updatedSubmission.getScore());
                    submission.setStatus(updatedSubmission.getStatus());
                    submission.setSubmittedAt(updatedSubmission.getSubmittedAt());
                    submission.setTimeSpentSeconds(updatedSubmission.getTimeSpentSeconds());
                    submission.setUser(updatedSubmission.getUser());
                    submission.setChallenge(updatedSubmission.getChallenge());
                    submission.setSession(updatedSubmission.getSession());
                    return codingSubmissionRepository.save(submission);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Coding submission not found with id: " + id));
    }

    @Override
    public void deleteSubmission(Long id) {
        codingSubmissionRepository.deleteById(id);
    }
}