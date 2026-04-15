package com.teamone.interviewprep.repository;

import com.teamone.interviewprep.entity.BehavioralSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BehavioralSubmissionRepository extends JpaRepository<BehavioralSubmission, Long> {
    List<BehavioralSubmission> findByUserId(Long userId);
    List<BehavioralSubmission> findBySessionId(Long sessionId);
    List<BehavioralSubmission> findByQuestionId(Long questionId);
}