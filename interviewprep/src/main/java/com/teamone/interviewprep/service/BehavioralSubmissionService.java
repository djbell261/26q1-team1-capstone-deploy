package com.teamone.interviewprep.service;

import com.teamone.interviewprep.entity.BehavioralSubmission;

import java.util.List;
import java.util.Optional;

public interface BehavioralSubmissionService {
    BehavioralSubmission createSubmission(BehavioralSubmission submission);
    List<BehavioralSubmission> getAllSubmissions();
    Optional<BehavioralSubmission> getSubmissionById(Long id);
    List<BehavioralSubmission> getSubmissionsByUserId(Long userId);
    List<BehavioralSubmission> getSubmissionsBySessionId(Long sessionId);
    List<BehavioralSubmission> getSubmissionsByQuestionId(Long questionId);
    BehavioralSubmission updateSubmission(Long id, BehavioralSubmission updatedSubmission);
    void deleteSubmission(Long id);
}