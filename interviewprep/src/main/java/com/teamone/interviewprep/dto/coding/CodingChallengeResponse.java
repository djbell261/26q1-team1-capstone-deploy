package com.teamone.interviewprep.dto.coding;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodingChallengeResponse {

    private Long id;
    private String externalApiId;
    private String title;
    private String description;
    private String difficulty;
    private String category;
    private Integer timeLimitMinutes;
}