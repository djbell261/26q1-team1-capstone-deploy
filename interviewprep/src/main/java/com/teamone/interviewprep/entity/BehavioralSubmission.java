package com.teamone.interviewprep.entity;

import com.teamone.interviewprep.enums.SubmissionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "behavioral_submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BehavioralSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String responseText;

    private Double score;

    private LocalDateTime submittedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private BehavioralQuestion question;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private AssessmentSession session;

    @OneToOne(mappedBy = "behavioralSubmission")
    private Feedback feedback;
}