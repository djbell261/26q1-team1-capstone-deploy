package com.teamone.interviewprep.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "behavioral_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BehavioralQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_text", columnDefinition = "TEXT")
    private String questionText;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String difficulty;

    @OneToMany(mappedBy = "question")
    private List<BehavioralSubmission> submissions;
}