package com.teamone.interviewprep.controller;

import com.teamone.interviewprep.dto.behavioral.BehavioralQuestionResponse;
import com.teamone.interviewprep.entity.BehavioralQuestion;
import com.teamone.interviewprep.mapper.BehavioralSubmissionMapper;
import com.teamone.interviewprep.service.BehavioralQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/behavioral-questions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class BehavioralQuestionController {

    private final BehavioralQuestionService behavioralQuestionService;
    private final BehavioralSubmissionMapper behavioralSubmissionMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<BehavioralQuestionResponse> createQuestion(@RequestBody BehavioralQuestion question) {
        BehavioralQuestion savedQuestion = behavioralQuestionService.createQuestion(question);
        return ResponseEntity.ok(behavioralSubmissionMapper.toQuestionResponse(savedQuestion));
    }

    @GetMapping
    public ResponseEntity<List<BehavioralQuestionResponse>> getAllQuestions() {
        List<BehavioralQuestionResponse> questions = behavioralQuestionService.getAllQuestions()
                .stream()
                .map(behavioralSubmissionMapper::toQuestionResponse)
                .toList();

        return ResponseEntity.ok(questions);
    }


    @GetMapping("/{id}")
    public ResponseEntity<BehavioralQuestionResponse> getQuestionById(@PathVariable Long id) {
        return behavioralQuestionService.getQuestionById(id)
                .map(behavioralSubmissionMapper::toQuestionResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<BehavioralQuestionResponse>> getQuestionsByCategory(@PathVariable String category) {
        List<BehavioralQuestionResponse> questions = behavioralQuestionService.getQuestionsByCategory(category)
                .stream()
                .map(behavioralSubmissionMapper::toQuestionResponse)
                .toList();

        return ResponseEntity.ok(questions);
    }

    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<BehavioralQuestionResponse>> getQuestionsByDifficulty(@PathVariable String difficulty) {
        List<BehavioralQuestionResponse> questions = behavioralQuestionService.getQuestionsByDifficulty(difficulty)
                .stream()
                .map(behavioralSubmissionMapper::toQuestionResponse)
                .toList();

        return ResponseEntity.ok(questions);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<BehavioralQuestionResponse> updateQuestion(@PathVariable Long id,
                                                                     @RequestBody BehavioralQuestion updatedQuestion) {
        BehavioralQuestion savedQuestion = behavioralQuestionService.updateQuestion(id, updatedQuestion);
        return ResponseEntity.ok(behavioralSubmissionMapper.toQuestionResponse(savedQuestion));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        behavioralQuestionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}