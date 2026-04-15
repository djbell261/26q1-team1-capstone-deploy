package com.teamone.interviewprep.repository;

import com.teamone.interviewprep.entity.BehavioralQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BehavioralQuestionRepository extends JpaRepository<BehavioralQuestion, Long> {
    List<BehavioralQuestion> findByCategory(String category);
    List<BehavioralQuestion> findByDifficulty(String difficulty);
}