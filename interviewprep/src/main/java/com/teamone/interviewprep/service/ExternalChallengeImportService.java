package com.teamone.interviewprep.service;

import com.teamone.interviewprep.entity.CodingChallenge;

import java.util.List;

public interface ExternalChallengeImportService {
    List<CodingChallenge> importChallenges();
}