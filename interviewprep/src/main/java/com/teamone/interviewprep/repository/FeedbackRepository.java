package com.teamone.interviewprep.repository;

import com.teamone.interviewprep.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Optional<Feedback> findByCodingSubmissionId(Long codingSubmissionId);
    Optional<Feedback> findByBehavioralSubmissionId(Long behavioralSubmissionId);
}