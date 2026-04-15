package com.teamone.interviewprep.mapper;

import com.teamone.interviewprep.dto.session.AssessmentSessionRequest;
import com.teamone.interviewprep.dto.session.AssessmentSessionResponse;
import com.teamone.interviewprep.entity.AssessmentSession;
import com.teamone.interviewprep.entity.User;
import com.teamone.interviewprep.enums.AssignmentType;
import com.teamone.interviewprep.enums.SessionStatus;
import org.springframework.stereotype.Component;

@Component
public class AssessmentSessionMapper {

    public AssessmentSession toEntity(AssessmentSessionRequest request, User user) {
        return AssessmentSession.builder()
                .type(AssignmentType.valueOf(request.getType().toUpperCase()))
                .startedAt(request.getStartedAt())
                .expiresAt(request.getExpiresAt())
                .status(SessionStatus.valueOf(request.getStatus().toUpperCase()))
                .user(user)
                .build();
    }

    public AssessmentSessionResponse toResponse(AssessmentSession session) {
        return AssessmentSessionResponse.builder()
                .id(session.getId())
                .type(session.getType().name())
                .startedAt(session.getStartedAt())
                .expiresAt(session.getExpiresAt())
                .status(session.getStatus().name())
                .userId(session.getUser() != null ? session.getUser().getId() : null)
                .build();
    }
}