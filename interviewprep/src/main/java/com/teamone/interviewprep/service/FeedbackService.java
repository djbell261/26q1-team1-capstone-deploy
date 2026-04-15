package com.teamone.interviewprep.service;

import com.teamone.interviewprep.entity.Feedback;

import java.util.List;
import java.util.Optional;

public interface FeedbackService {
    Feedback createFeedback(Feedback feedback);
    List<Feedback> getAllFeedback();
    Optional<Feedback> getFeedbackById(Long id);
    Optional<Feedback> getFeedbackByCodingSubmissionId(Long codingSubmissionId);
    Optional<Feedback> getFeedbackByBehavioralSubmissionId(Long behavioralSubmissionId);
    Feedback updateFeedback(Long id, Feedback updatedFeedback);
    void deleteFeedback(Long id);
}