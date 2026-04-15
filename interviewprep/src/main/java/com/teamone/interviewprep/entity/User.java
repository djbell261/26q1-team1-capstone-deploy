package com.teamone.interviewprep.entity;

import com.teamone.interviewprep.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @OneToMany(mappedBy = "user")
    private List<CodingSubmission> codingSubmissions;

    @OneToMany(mappedBy = "user")
    private List<BehavioralSubmission> behavioralSubmissions;

    @OneToMany(mappedBy = "user")
    private List<AssessmentSession> sessions;

    @OneToMany(mappedBy = "user")
    private List<Recommendation> recommendations;
}