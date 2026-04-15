package com.teamone.interviewprep.service;

import com.teamone.interviewprep.entity.BehavioralQuestion;

import java.util.List;
import java.util.Optional;

public interface BehavioralQuestionService {
    BehavioralQuestion createQuestion(BehavioralQuestion question);
    List<BehavioralQuestion> getAllQuestions();
    Optional<BehavioralQuestion> getQuestionById(Long id);
    List<BehavioralQuestion> getQuestionsByCategory(String category);
    List<BehavioralQuestion> getQuestionsByDifficulty(String difficulty);
    BehavioralQuestion updateQuestion(Long id, BehavioralQuestion updatedQuestion);
    void deleteQuestion(Long id);
}