package com.teamone.interviewprep.dto.feedback;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackResponse {

    private Long id;
    private String type;
    private Double aiScore;
    private String summary;
    private String strengths;
    private String weaknesses;
    private String recommendations;
    private String status;
    private LocalDateTime generatedAt;
    private Long codingSubmissionId;
    private Long behavioralSubmissionId;
}