package com.teamone.interviewprep.dto.session;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentSessionRequest {

    private String type;
    private LocalDateTime startedAt;
    private LocalDateTime expiresAt;
    private String status;

}