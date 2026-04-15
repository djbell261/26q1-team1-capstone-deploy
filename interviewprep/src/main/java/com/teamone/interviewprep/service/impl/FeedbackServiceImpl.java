package com.teamone.interviewprep.service.impl;

import com.teamone.interviewprep.entity.Feedback;
import com.teamone.interviewprep.exception.ResourceNotFoundException;
import com.teamone.interviewprep.repository.FeedbackRepository;
import com.teamone.interviewprep.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;

    @Override
    public Feedback createFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    @Override
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    @Override
    public Optional<Feedback> getFeedbackById(Long id) {
        return feedbackRepository.findById(id);
    }

    @Override
    public Optional<Feedback> getFeedbackByCodingSubmissionId(Long codingSubmissionId) {
        return feedbackRepository.findByCodingSubmissionId(codingSubmissionId);
    }

    @Override
    public Optional<Feedback> getFeedbackByBehavioralSubmissionId(Long behavioralSubmissionId) {
        return feedbackRepository.findByBehavioralSubmissionId(behavioralSubmissionId);
    }

    @Override
    public Feedback updateFeedback(Long id, Feedback updatedFeedback) {
        return feedbackRepository.findById(id)
                .map(feedback -> {
                    feedback.setType(updatedFeedback.getType());
                    feedback.setAiScore(updatedFeedback.getAiScore());
                    feedback.setSummary(updatedFeedback.getSummary());
                    feedback.setStrengths(updatedFeedback.getStrengths());
                    feedback.setWeaknesses(updatedFeedback.getWeaknesses());
                    feedback.setRecommendations(updatedFeedback.getRecommendations());
                    feedback.setStatus(updatedFeedback.getStatus());
                    feedback.setGeneratedAt(updatedFeedback.getGeneratedAt());
                    feedback.setCodingSubmission(updatedFeedback.getCodingSubmission());
                    feedback.setBehavioralSubmission(updatedFeedback.getBehavioralSubmission());
                    return feedbackRepository.save(feedback);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + id));
    }

    @Override
    public void deleteFeedback(Long id) {
        feedbackRepository.deleteById(id);
    }
}