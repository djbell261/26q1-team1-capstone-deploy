package com.teamone.interviewprep.external.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalChallengeDto {
    private String externalId;
    private String title;
    private String description;
    private String difficulty;
    private String category;
    private Integer timeLimitMinutes;
}