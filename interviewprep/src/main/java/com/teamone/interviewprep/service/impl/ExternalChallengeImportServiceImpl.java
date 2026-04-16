package com.teamone.interviewprep.service.impl;

import com.teamone.interviewprep.entity.CodingChallenge;
import com.teamone.interviewprep.external.client.ExternalCodingApiClient;
import com.teamone.interviewprep.external.dto.ExternalChallengeDto;
import com.teamone.interviewprep.repository.CodingChallengeRepository;
import com.teamone.interviewprep.service.ExternalChallengeImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExternalChallengeImportServiceImpl implements ExternalChallengeImportService {

    private final ExternalCodingApiClient externalCodingApiClient;
    private final CodingChallengeRepository codingChallengeRepository;

    @Override
    @Transactional
    public List<CodingChallenge> importChallenges() {
        List<ExternalChallengeDto> externalChallenges = externalCodingApiClient.fetchChallenges();
        List<CodingChallenge> savedChallenges = new ArrayList<>();

        for (ExternalChallengeDto dto : externalChallenges) {
            if (dto.getExternalId() == null || dto.getExternalId().isBlank()) {
                continue;
            }

            Optional<CodingChallenge> existingOpt =
                    codingChallengeRepository.findByExternalApiId(dto.getExternalId());

            CodingChallenge challenge = existingOpt.orElseGet(CodingChallenge::new);

            challenge.setExternalApiId(dto.getExternalId());
            challenge.setTitle(defaultText(dto.getTitle(), "Untitled Challenge"));
            challenge.setDescription(defaultText(dto.getDescription(), "No description provided."));
            challenge.setDifficulty(normalizeDifficulty(dto.getDifficulty()));
            challenge.setCategory(defaultText(dto.getCategory(), "General"));
            challenge.setTimeLimitMinutes(resolveTimeLimit(dto));

            savedChallenges.add(codingChallengeRepository.save(challenge));
        }

        return savedChallenges;
    }

    private String defaultText(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value.trim();
    }

    private String normalizeDifficulty(String difficulty) {
        if (difficulty == null || difficulty.isBlank()) {
            return "MEDIUM";
        }

        String normalized = difficulty.trim().toUpperCase(Locale.ROOT);

        return switch (normalized) {
            case "EASY" -> "EASY";
            case "MEDIUM" -> "MEDIUM";
            case "HARD" -> "HARD";
            default -> "MEDIUM";
        };
    }

    private Integer resolveTimeLimit(ExternalChallengeDto dto) {
        if (dto.getTimeLimitMinutes() != null && dto.getTimeLimitMinutes() > 0) {
            return dto.getTimeLimitMinutes();
        }

        return switch (normalizeDifficulty(dto.getDifficulty())) {
            case "EASY" -> 20;
            case "HARD" -> 45;
            default -> 30;
        };
    }
}