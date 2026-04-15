package com.teamone.interviewprep.mapper;

import com.teamone.interviewprep.dto.behavioral.BehavioralQuestionResponse;
import com.teamone.interviewprep.dto.behavioral.BehavioralSubmissionRequest;
import com.teamone.interviewprep.dto.behavioral.BehavioralSubmissionResponse;
import com.teamone.interviewprep.entity.AssessmentSession;
import com.teamone.interviewprep.entity.BehavioralQuestion;
import com.teamone.interviewprep.entity.BehavioralSubmission;
import com.teamone.interviewprep.entity.User;
import com.teamone.interviewprep.enums.SubmissionStatus;
import org.springframework.stereotype.Component;

@Component
public class BehavioralSubmissionMapper {

    public BehavioralSubmission toEntity(BehavioralSubmissionRequest request,
                                         User user,
                                         BehavioralQuestion question,
                                         AssessmentSession session) {
        return BehavioralSubmission.builder()
                .responseText(request.getResponseText())
                .score(request.getScore())
                .submittedAt(request.getSubmittedAt())
                .status(SubmissionStatus.valueOf(request.getStatus().toUpperCase()))
                .user(user)
                .question(question)
                .session(session)
                .build();
    }

    public BehavioralSubmissionResponse toResponse(BehavioralSubmission submission) {
        return BehavioralSubmissionResponse.builder()
                .id(submission.getId())
                .responseText(submission.getResponseText())
                .score(submission.getScore())
                .submittedAt(submission.getSubmittedAt())
                .status(submission.getStatus().name())
                .userId(submission.getUser() != null ? submission.getUser().getId() : null)
                .questionId(submission.getQuestion() != null ? submission.getQuestion().getId() : null)
                .sessionId(submission.getSession() != null ? submission.getSession().getId() : null)
                .build();
    }

    public BehavioralQuestionResponse toQuestionResponse(BehavioralQuestion question) {
        return BehavioralQuestionResponse.builder()
                .id(question.getId())
                .questionText(question.getQuestionText())
                .category(question.getCategory())
                .difficulty(question.getDifficulty())
                .build();
    }
}