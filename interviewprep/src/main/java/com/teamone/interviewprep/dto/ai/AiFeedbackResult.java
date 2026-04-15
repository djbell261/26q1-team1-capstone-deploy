package com.teamone.interviewprep.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiFeedbackResult {
    private Double aiScore;
    private String summary;
    private String strengths;
    private String weaknesses;
    private String recommendations;
}