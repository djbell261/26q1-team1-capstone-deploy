package com.teamone.interviewprep.entity;

import com.teamone.interviewprep.enums.FeedbackStatus;
import com.teamone.interviewprep.enums.FeedbackType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackType type;

    private Double aiScore;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String strengths;

    @Column(columnDefinition = "TEXT")
    private String weaknesses;

    @Column(columnDefinition = "TEXT")
    private String recommendations;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackStatus status;

    private LocalDateTime generatedAt;

    @OneToOne
    @JoinColumn(name = "coding_submission_id")
    private CodingSubmission codingSubmission;

    @OneToOne
    @JoinColumn(name = "behavioral_submission_id")
    private BehavioralSubmission behavioralSubmission;
}