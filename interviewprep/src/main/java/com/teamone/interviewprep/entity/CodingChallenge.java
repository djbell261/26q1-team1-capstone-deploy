package com.teamone.interviewprep.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "coding_challenges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodingChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_api_id")
    private String externalApiId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String difficulty;

    @Column(nullable = false)
    private String category;

    @Column(name = "time_limit_minutes", nullable = false)
    private Integer timeLimitMinutes;

    @OneToMany(mappedBy = "challenge")
    private List<CodingSubmission> submissions;
}