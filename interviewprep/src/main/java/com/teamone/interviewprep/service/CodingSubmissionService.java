package com.teamone.interviewprep.service;

import com.teamone.interviewprep.entity.CodingSubmission;

import java.util.List;
import java.util.Optional;

public interface CodingSubmissionService {
    CodingSubmission createSubmission(CodingSubmission submission);
    List<CodingSubmission> getAllSubmissions();
    Optional<CodingSubmission> getSubmissionById(Long id);
    List<CodingSubmission> getSubmissionsByUserId(Long userId);
    List<CodingSubmission> getSubmissionsBySessionId(Long sessionId);
    List<CodingSubmission> getSubmissionsByChallengeId(Long challengeId);
    CodingSubmission updateSubmission(Long id, CodingSubmission updatedSubmission);
    void deleteSubmission(Long id);
}