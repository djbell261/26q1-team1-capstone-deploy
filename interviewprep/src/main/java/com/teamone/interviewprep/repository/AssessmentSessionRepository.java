package com.teamone.interviewprep.repository;

import com.teamone.interviewprep.entity.AssessmentSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssessmentSessionRepository extends JpaRepository<AssessmentSession, Long> {
    List<AssessmentSession> findByUserId(Long userId);
    List<AssessmentSession> findByStatus(String status);
}