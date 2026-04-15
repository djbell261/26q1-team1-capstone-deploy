package com.teamone.interviewprep.mapper;

import com.teamone.interviewprep.dto.coding.CodingChallengeResponse;
import com.teamone.interviewprep.dto.coding.CodingSubmissionRequest;
import com.teamone.interviewprep.dto.coding.CodingSubmissionResponse;
import com.teamone.interviewprep.entity.AssessmentSession;
import com.teamone.interviewprep.entity.CodingChallenge;
import com.teamone.interviewprep.entity.CodingSubmission;
import com.teamone.interviewprep.entity.User;
import com.teamone.interviewprep.enums.SubmissionStatus;
import org.springframework.stereotype.Component;

@Component
public class CodingSubmissionMapper {

    public CodingSubmission toEntity(CodingSubmissionRequest request,
                                     User user,
                                     CodingChallenge challenge,
                                     AssessmentSession session) {

        SubmissionStatus status = (request.getStatus() == null || request.getStatus().isBlank())
                ? SubmissionStatus.SUBMITTED
                : SubmissionStatus.valueOf(request.getStatus().toUpperCase());

        return CodingSubmission.builder()
                .code(request.getCode())
                .score(request.getScore())
                .status(status)
                .submittedAt(request.getSubmittedAt())
                .timeSpentSeconds(request.getTimeSpentSeconds())
                .user(user)
                .challenge(challenge)
                .session(session)
                .build();
    }

    public CodingSubmissionResponse toResponse(CodingSubmission submission) {
        return CodingSubmissionResponse.builder()
                .id(submission.getId())
                .code(submission.getCode())
                .score(submission.getScore())
                .status(submission.getStatus().name())
                .submittedAt(submission.getSubmittedAt())
                .timeSpentSeconds(submission.getTimeSpentSeconds())
                .userId(submission.getUser() != null ? submission.getUser().getId() : null)
                .challengeId(submission.getChallenge() != null ? submission.getChallenge().getId() : null)
                .sessionId(submission.getSession() != null ? submission.getSession().getId() : null)
                .build();
    }

    public CodingChallengeResponse toChallengeResponse(CodingChallenge challenge) {
        return CodingChallengeResponse.builder()
                .id(challenge.getId())
                .externalApiId(challenge.getExternalApiId())
                .title(challenge.getTitle())
                .description(challenge.getDescription())
                .difficulty(challenge.getDifficulty())
                .category(challenge.getCategory())
                .timeLimitMinutes(challenge.getTimeLimitMinutes())
                .build();
    }
}