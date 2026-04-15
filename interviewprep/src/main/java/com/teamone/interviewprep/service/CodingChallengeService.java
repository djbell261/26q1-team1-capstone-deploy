package com.teamone.interviewprep.service;

import com.teamone.interviewprep.entity.CodingChallenge;

import java.util.List;
import java.util.Optional;

public interface CodingChallengeService {
    CodingChallenge createChallenge(CodingChallenge challenge);
    List<CodingChallenge> getAllChallenges();
    Optional<CodingChallenge> getChallengeById(Long id);
    List<CodingChallenge> getChallengesByDifficulty(String difficulty);
    List<CodingChallenge> getChallengesByCategory(String category);
    CodingChallenge updateChallenge(Long id, CodingChallenge updatedChallenge);
    void deleteChallenge(Long id);
}