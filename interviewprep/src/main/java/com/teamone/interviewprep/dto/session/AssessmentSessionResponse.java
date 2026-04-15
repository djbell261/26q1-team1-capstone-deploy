package com.teamone.interviewprep.dto.session;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentSessionResponse {

    private Long id;
    private String type;
    private LocalDateTime startedAt;
    private LocalDateTime expiresAt;
    private String status;
    private Long userId;
}