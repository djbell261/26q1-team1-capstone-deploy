package com.teamone.interviewprep.dto.behavioral;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BehavioralQuestionResponse {

    private Long id;
    private String questionText;
    private String category;
    private String difficulty;
}