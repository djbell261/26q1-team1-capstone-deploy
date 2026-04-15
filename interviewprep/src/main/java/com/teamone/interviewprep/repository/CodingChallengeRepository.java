package com.teamone.interviewprep.repository;

import com.teamone.interviewprep.entity.CodingChallenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CodingChallengeRepository extends JpaRepository<CodingChallenge, Long> {
    List<CodingChallenge> findByDifficulty(String difficulty);
    List<CodingChallenge> findByCategory(String category);
}