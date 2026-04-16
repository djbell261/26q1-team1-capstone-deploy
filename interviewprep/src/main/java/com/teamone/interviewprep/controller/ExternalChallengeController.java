package com.teamone.interviewprep.controller;

import com.teamone.interviewprep.dto.coding.CodingChallengeResponse;
import com.teamone.interviewprep.entity.CodingChallenge;
import com.teamone.interviewprep.mapper.CodingSubmissionMapper;
import com.teamone.interviewprep.service.ExternalChallengeImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/challenges")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ExternalChallengeController {

    private final ExternalChallengeImportService externalChallengeImportService;
    private final CodingSubmissionMapper codingSubmissionMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/import")
    public ResponseEntity<List<CodingChallengeResponse>> importChallenges() {
        List<CodingChallenge> imported = externalChallengeImportService.importChallenges();

        List<CodingChallengeResponse> response = imported.stream()
                .map(codingSubmissionMapper::toChallengeResponse)
                .toList();

        return ResponseEntity.ok(response);
    }
}