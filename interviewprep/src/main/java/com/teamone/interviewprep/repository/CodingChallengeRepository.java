package com.teamone.interviewprep.repository;

import com.teamone.interviewprep.entity.CodingChallenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CodingChallengeRepository extends JpaRepository<CodingChallenge, Long> {
    List<CodingChallenge> findByDifficulty(String difficulty);
    List<CodingChallenge> findByCategory(String category);
    Optional<CodingChallenge> findByExternalApiId(String externalApiId);
}