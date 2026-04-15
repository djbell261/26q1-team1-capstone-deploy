package com.teamone.interviewprep.service;

import com.teamone.interviewprep.entity.Feedback;

public interface AiService {
    Feedback generateCodingFeedback(Long codingSubmissionId);
    Feedback generateBehavioralFeedback(Long behavioralSubmissionId);
}