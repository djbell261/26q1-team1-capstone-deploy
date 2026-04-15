package com.teamone.interviewprep.dto.behavioral;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BehavioralSubmissionResponse {

    private Long id;
    private String responseText;
    private Double score;
    private LocalDateTime submittedAt;
    private String status;
    private Long userId;
    private Long questionId;
    private Long sessionId;
}