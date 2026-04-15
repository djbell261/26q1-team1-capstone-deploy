package com.teamone.interviewprep.controller;

import com.teamone.interviewprep.dto.coding.CodingChallengeResponse;
import com.teamone.interviewprep.entity.CodingChallenge;
import com.teamone.interviewprep.mapper.CodingSubmissionMapper;
import com.teamone.interviewprep.service.CodingChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coding-challenges")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class CodingChallengeController {

    private final CodingChallengeService codingChallengeService;
    private final CodingSubmissionMapper codingSubmissionMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CodingChallengeResponse> createChallenge(@RequestBody CodingChallenge challenge) {
        CodingChallenge savedChallenge = codingChallengeService.createChallenge(challenge);
        return ResponseEntity.ok(codingSubmissionMapper.toChallengeResponse(savedChallenge));
    }

    @GetMapping
    public ResponseEntity<List<CodingChallengeResponse>> getAllChallenges() {
        List<CodingChallengeResponse> challenges = codingChallengeService.getAllChallenges()
                .stream()
                .map(codingSubmissionMapper::toChallengeResponse)
                .toList();

        return ResponseEntity.ok(challenges);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CodingChallengeResponse> getChallengeById(@PathVariable Long id) {
        return codingChallengeService.getChallengeById(id)
                .map(codingSubmissionMapper::toChallengeResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<CodingChallengeResponse>> getChallengesByDifficulty(@PathVariable String difficulty) {
        List<CodingChallengeResponse> challenges = codingChallengeService.getChallengesByDifficulty(difficulty)
                .stream()
                .map(codingSubmissionMapper::toChallengeResponse)
                .toList();

        return ResponseEntity.ok(challenges);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<CodingChallengeResponse>> getChallengesByCategory(@PathVariable String category) {
        List<CodingChallengeResponse> challenges = codingChallengeService.getChallengesByCategory(category)
                .stream()
                .map(codingSubmissionMapper::toChallengeResponse)
                .toList();

        return ResponseEntity.ok(challenges);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CodingChallengeResponse> updateChallenge(@PathVariable Long id,
                                                                   @RequestBody CodingChallenge updatedChallenge) {
        CodingChallenge savedChallenge = codingChallengeService.updateChallenge(id, updatedChallenge);
        return ResponseEntity.ok(codingSubmissionMapper.toChallengeResponse(savedChallenge));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChallenge(@PathVariable Long id) {
        codingChallengeService.deleteChallenge(id);
        return ResponseEntity.noContent().build();
    }
}