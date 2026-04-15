package com.teamone.interviewprep.service.impl;

import com.teamone.interviewprep.entity.BehavioralQuestion;
import com.teamone.interviewprep.exception.ResourceNotFoundException;
import com.teamone.interviewprep.repository.BehavioralQuestionRepository;
import com.teamone.interviewprep.service.BehavioralQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BehavioralQuestionServiceImpl implements BehavioralQuestionService {

    private final BehavioralQuestionRepository behavioralQuestionRepository;

    @Override
    public BehavioralQuestion createQuestion(BehavioralQuestion question) {
        return behavioralQuestionRepository.save(question);
    }

    @Override
    public List<BehavioralQuestion> getAllQuestions() {
        return behavioralQuestionRepository.findAll();
    }

    @Override
    public Optional<BehavioralQuestion> getQuestionById(Long id) {
        return behavioralQuestionRepository.findById(id);
    }

    @Override
    public List<BehavioralQuestion> getQuestionsByCategory(String category) {
        return behavioralQuestionRepository.findByCategory(category);
    }

    @Override
    public List<BehavioralQuestion> getQuestionsByDifficulty(String difficulty) {
        return behavioralQuestionRepository.findByDifficulty(difficulty);
    }

    @Override
    public BehavioralQuestion updateQuestion(Long id, BehavioralQuestion updatedQuestion) {
        return behavioralQuestionRepository.findById(id)
                .map(question -> {
                    question.setQuestionText(updatedQuestion.getQuestionText());
                    question.setCategory(updatedQuestion.getCategory());
                    question.setDifficulty(updatedQuestion.getDifficulty());
                    return behavioralQuestionRepository.save(question);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Behavioral question not found with id: " + id));
    }

    @Override
    public void deleteQuestion(Long id) {
        behavioralQuestionRepository.deleteById(id);
    }
}