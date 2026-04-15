package com.teamone.interviewprep.dto.coding;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodingSubmissionRequest {

    private String code;
    private Double score;
    private String status;
    private LocalDateTime submittedAt;
    private Integer timeSpentSeconds;
    private Long challengeId;
    private Long sessionId;
}