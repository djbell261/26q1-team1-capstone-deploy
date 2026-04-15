package com.teamone.interviewprep.service.impl;

import com.teamone.interviewprep.entity.BehavioralSubmission;
import com.teamone.interviewprep.exception.ResourceNotFoundException;
import com.teamone.interviewprep.repository.BehavioralSubmissionRepository;
import com.teamone.interviewprep.service.BehavioralSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BehavioralSubmissionServiceImpl implements BehavioralSubmissionService {

    private final BehavioralSubmissionRepository behavioralSubmissionRepository;

    @Override
    public BehavioralSubmission createSubmission(BehavioralSubmission submission) {
        return behavioralSubmissionRepository.save(submission);
    }

    @Override
    public List<BehavioralSubmission> getAllSubmissions() {
        return behavioralSubmissionRepository.findAll();
    }

    @Override
    public Optional<BehavioralSubmission> getSubmissionById(Long id) {
        return behavioralSubmissionRepository.findById(id);
    }

    @Override
    public List<BehavioralSubmission> getSubmissionsByUserId(Long userId) {
        return behavioralSubmissionRepository.findByUserId(userId);
    }

    @Override
    public List<BehavioralSubmission> getSubmissionsBySessionId(Long sessionId) {
        return behavioralSubmissionRepository.findBySessionId(sessionId);
    }

    @Override
    public List<BehavioralSubmission> getSubmissionsByQuestionId(Long questionId) {
        return behavioralSubmissionRepository.findByQuestionId(questionId);
    }

    @Override
    public BehavioralSubmission updateSubmission(Long id, BehavioralSubmission updatedSubmission) {
        return behavioralSubmissionRepository.findById(id)
                .map(submission -> {
                    submission.setResponseText(updatedSubmission.getResponseText());
                    submission.setScore(updatedSubmission.getScore());
                    submission.setSubmittedAt(updatedSubmission.getSubmittedAt());
                    submission.setUser(updatedSubmission.getUser());
                    submission.setQuestion(updatedSubmission.getQuestion());
                    submission.setSession(updatedSubmission.getSession());
                    return behavioralSubmissionRepository.save(submission);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Behavioral submission not found with id: " + id));
    }

    @Override
    public void deleteSubmission(Long id) {
        behavioralSubmissionRepository.deleteById(id);
    }
}