package com.teamone.interviewprep.repository;

import com.teamone.interviewprep.entity.CodingSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CodingSubmissionRepository extends JpaRepository<CodingSubmission, Long> {
    List<CodingSubmission> findByUserId(Long userId);
    List<CodingSubmission> findBySessionId(Long sessionId);
    List<CodingSubmission> findByChallengeId(Long challengeId);
}