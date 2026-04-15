package com.teamone.interviewprep.service.impl;

import com.teamone.interviewprep.entity.CodingChallenge;
import com.teamone.interviewprep.exception.ResourceNotFoundException;
import com.teamone.interviewprep.repository.CodingChallengeRepository;
import com.teamone.interviewprep.service.CodingChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CodingChallengeServiceImpl implements CodingChallengeService {

    private final CodingChallengeRepository codingChallengeRepository;

    @Override
    public CodingChallenge createChallenge(CodingChallenge challenge) {
        return codingChallengeRepository.save(challenge);
    }

    @Override
    public List<CodingChallenge> getAllChallenges() {
        return codingChallengeRepository.findAll();
    }

    @Override
    public Optional<CodingChallenge> getChallengeById(Long id) {
        return codingChallengeRepository.findById(id);
    }

    @Override
    public List<CodingChallenge> getChallengesByDifficulty(String difficulty) {
        return codingChallengeRepository.findByDifficulty(difficulty);
    }

    @Override
    public List<CodingChallenge> getChallengesByCategory(String category) {
        return codingChallengeRepository.findByCategory(category);
    }

    @Override
    public CodingChallenge updateChallenge(Long id, CodingChallenge updatedChallenge) {
        return codingChallengeRepository.findById(id)
                .map(challenge -> {
                    challenge.setExternalApiId(updatedChallenge.getExternalApiId());
                    challenge.setTitle(updatedChallenge.getTitle());
                    challenge.setDescription(updatedChallenge.getDescription());
                    challenge.setDifficulty(updatedChallenge.getDifficulty());
                    challenge.setCategory(updatedChallenge.getCategory());
                    challenge.setTimeLimitMinutes(updatedChallenge.getTimeLimitMinutes());
                    return codingChallengeRepository.save(challenge);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found with id: " + id));
    }

    @Override
    public void deleteChallenge(Long id) {
        codingChallengeRepository.deleteById(id);
    }
}