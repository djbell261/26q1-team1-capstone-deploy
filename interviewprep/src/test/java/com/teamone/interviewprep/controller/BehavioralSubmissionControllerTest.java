package com.teamone.interviewprep.controller;

import com.teamone.interviewprep.dto.behavioral.BehavioralSubmissionRequest;
import com.teamone.interviewprep.dto.behavioral.BehavioralSubmissionResponse;
import com.teamone.interviewprep.entity.AssessmentSession;
import com.teamone.interviewprep.entity.BehavioralQuestion;
import com.teamone.interviewprep.entity.BehavioralSubmission;
import com.teamone.interviewprep.entity.User;
import com.teamone.interviewprep.exception.ResourceNotFoundException;
import com.teamone.interviewprep.exception.SessionExpiredException;
import com.teamone.interviewprep.exception.UnauthorizedException;
import com.teamone.interviewprep.mapper.BehavioralSubmissionMapper;
import com.teamone.interviewprep.security.SecurityUtils;
import com.teamone.interviewprep.service.AiService;
import com.teamone.interviewprep.service.AssessmentSessionService;
import com.teamone.interviewprep.service.BehavioralQuestionService;
import com.teamone.interviewprep.service.BehavioralSubmissionService;
import com.teamone.interviewprep.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BehavioralSubmissionControllerTest {

    @Mock
    private BehavioralSubmissionService behavioralSubmissionService;

    @Mock
    private UserService userService;

    @Mock
    private BehavioralQuestionService behavioralQuestionService;

    @Mock
    private AssessmentSessionService assessmentSessionService;

    @Mock
    private BehavioralSubmissionMapper behavioralSubmissionMapper;

    @Mock
    private AiService aiService;

    @InjectMocks
    private BehavioralSubmissionController controller;

    private User user;
    private User otherUser;
    private BehavioralQuestion question;
    private AssessmentSession session;
    private BehavioralSubmissionRequest request;
    private BehavioralSubmission submission;
    private BehavioralSubmissionResponse response;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setEmail("other@example.com");

        question = new BehavioralQuestion();
        question.setId(10L);

        session = new AssessmentSession();
        session.setId(20L);
        session.setUser(user);
        session.setExpiresAt(LocalDateTime.now().plusMinutes(30));

        request = new BehavioralSubmissionRequest();
        request.setQuestionId(10L);
        request.setSessionId(20L);

        submission = new BehavioralSubmission();
        submission.setId(30L);
        submission.setUser(user);

        response = new BehavioralSubmissionResponse();
        response.setId(30L);
    }

    @Test
    void createSubmission_shouldReturnUpdatedResponse_whenValid() {
        BehavioralSubmission mappedSubmission = new BehavioralSubmission();

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");

            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(behavioralQuestionService.getQuestionById(10L)).thenReturn(Optional.of(question));
            when(assessmentSessionService.getSessionById(20L)).thenReturn(Optional.of(session));
            when(behavioralSubmissionMapper.toEntity(request, user, question, session)).thenReturn(mappedSubmission);
            when(behavioralSubmissionService.createSubmission(mappedSubmission)).thenReturn(submission);
            when(behavioralSubmissionService.getSubmissionById(30L)).thenReturn(Optional.of(submission));
            when(behavioralSubmissionMapper.toResponse(submission)).thenReturn(response);

            ResponseEntity<BehavioralSubmissionResponse> result = controller.createSubmission(request);

            assertEquals(200, result.getStatusCode().value());
            assertNotNull(result.getBody());
            assertEquals(30L, result.getBody().getId());
            verify(aiService).generateBehavioralFeedback(30L);
        }
    }

    @Test
    void createSubmission_shouldThrowQuestionNotFound_whenQuestionMissing() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(behavioralQuestionService.getQuestionById(10L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> controller.createSubmission(request));
        }
    }

    @Test
    void createSubmission_shouldThrowSessionNotFound_whenSessionMissing() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(behavioralQuestionService.getQuestionById(10L)).thenReturn(Optional.of(question));
            when(assessmentSessionService.getSessionById(20L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> controller.createSubmission(request));
        }
    }

    @Test
    void createSubmission_shouldThrowUnauthorized_whenWrongSessionOwner() {
        session.setUser(otherUser);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(behavioralQuestionService.getQuestionById(10L)).thenReturn(Optional.of(question));
            when(assessmentSessionService.getSessionById(20L)).thenReturn(Optional.of(session));

            assertThrows(UnauthorizedException.class, () -> controller.createSubmission(request));
        }
    }

    @Test
    void createSubmission_shouldThrowRuntimeException_whenSessionExpired() {
        session.setExpiresAt(LocalDateTime.now().minusMinutes(1));

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(behavioralQuestionService.getQuestionById(10L)).thenReturn(Optional.of(question));
            when(assessmentSessionService.getSessionById(20L)).thenReturn(Optional.of(session));

            RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.createSubmission(request));
            assertEquals("Session has expired", ex.getMessage());
        }
    }

    @Test
    void getMySubmissions_shouldReturnMappedList() {
        BehavioralSubmission submission2 = new BehavioralSubmission();
        submission2.setId(31L);
        submission2.setUser(user);

        BehavioralSubmissionResponse response2 = new BehavioralSubmissionResponse();
        response2.setId(31L);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(behavioralSubmissionService.getSubmissionsByUserId(1L)).thenReturn(List.of(submission, submission2));
            when(behavioralSubmissionMapper.toResponse(submission)).thenReturn(response);
            when(behavioralSubmissionMapper.toResponse(submission2)).thenReturn(response2);

            ResponseEntity<List<BehavioralSubmissionResponse>> result = controller.getMySubmissions();

            assertEquals(200, result.getStatusCode().value());
            assertEquals(2, result.getBody().size());
        }
    }

    @Test
    void getSubmissionById_shouldReturnResponse_whenOwnedByCurrentUser() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(behavioralSubmissionService.getSubmissionById(30L)).thenReturn(Optional.of(submission));
            when(behavioralSubmissionMapper.toResponse(submission)).thenReturn(response);

            ResponseEntity<BehavioralSubmissionResponse> result = controller.getSubmissionById(30L);

            assertEquals(200, result.getStatusCode().value());
            assertNotNull(result.getBody());
            assertEquals(30L, result.getBody().getId());
        }
    }

    @Test
    void getSubmissionById_shouldThrowNotFound_whenMissing() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(behavioralSubmissionService.getSubmissionById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> controller.getSubmissionById(99L));
        }
    }

    @Test
    void getSubmissionById_shouldThrowUnauthorized_whenNotOwner() {
        submission.setUser(otherUser);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(behavioralSubmissionService.getSubmissionById(30L)).thenReturn(Optional.of(submission));

            assertThrows(UnauthorizedException.class, () -> controller.getSubmissionById(30L));
        }
    }

    @Test
    void updateSubmission_shouldReturnUpdatedResponse_whenValid() {
        BehavioralSubmission mappedUpdated = new BehavioralSubmission();
        BehavioralSubmission savedUpdated = new BehavioralSubmission();
        savedUpdated.setId(30L);
        savedUpdated.setUser(user);

        BehavioralSubmissionResponse savedResponse = new BehavioralSubmissionResponse();
        savedResponse.setId(30L);

        BehavioralSubmission existing = new BehavioralSubmission();
        existing.setId(30L);
        existing.setUser(user);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(behavioralSubmissionService.getSubmissionById(30L)).thenReturn(Optional.of(existing));
            when(behavioralQuestionService.getQuestionById(10L)).thenReturn(Optional.of(question));
            when(assessmentSessionService.getSessionById(20L)).thenReturn(Optional.of(session));
            when(behavioralSubmissionMapper.toEntity(request, user, question, session)).thenReturn(mappedUpdated);
            when(behavioralSubmissionService.updateSubmission(30L, mappedUpdated)).thenReturn(savedUpdated);
            when(behavioralSubmissionMapper.toResponse(savedUpdated)).thenReturn(savedResponse);

            ResponseEntity<BehavioralSubmissionResponse> result = controller.updateSubmission(30L, request);

            assertEquals(200, result.getStatusCode().value());
            assertNotNull(result.getBody());
            assertEquals(30L, result.getBody().getId());
        }
    }

    @Test
    void updateSubmission_shouldThrowNotFound_whenSubmissionMissing() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(behavioralSubmissionService.getSubmissionById(30L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> controller.updateSubmission(30L, request));
        }
    }

    @Test
    void updateSubmission_shouldThrowUnauthorized_whenNotOwner() {
        BehavioralSubmission existing = new BehavioralSubmission();
        existing.setId(30L);
        existing.setUser(otherUser);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(behavioralSubmissionService.getSubmissionById(30L)).thenReturn(Optional.of(existing));

            assertThrows(UnauthorizedException.class, () -> controller.updateSubmission(30L, request));
        }
    }

    @Test
    void updateSubmission_shouldThrowQuestionNotFound_whenQuestionMissing() {
        BehavioralSubmission existing = new BehavioralSubmission();
        existing.setId(30L);
        existing.setUser(user);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(behavioralSubmissionService.getSubmissionById(30L)).thenReturn(Optional.of(existing));
            when(behavioralQuestionService.getQuestionById(10L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> controller.updateSubmission(30L, request));
        }
    }

    @Test
    void updateSubmission_shouldThrowSessionNotFound_whenSessionMissing() {
        BehavioralSubmission existing = new BehavioralSubmission();
        existing.setId(30L);
        existing.setUser(user);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(behavioralSubmissionService.getSubmissionById(30L)).thenReturn(Optional.of(existing));
            when(behavioralQuestionService.getQuestionById(10L)).thenReturn(Optional.of(question));
            when(assessmentSessionService.getSessionById(20L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> controller.updateSubmission(30L, request));
        }
    }

    @Test
    void updateSubmission_shouldThrowExpired_whenSessionExpired() {
        BehavioralSubmission existing = new BehavioralSubmission();
        existing.setId(30L);
        existing.setUser(user);

        session.setExpiresAt(LocalDateTime.now().minusMinutes(5));

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(behavioralSubmissionService.getSubmissionById(30L)).thenReturn(Optional.of(existing));
            when(behavioralQuestionService.getQuestionById(10L)).thenReturn(Optional.of(question));
            when(assessmentSessionService.getSessionById(20L)).thenReturn(Optional.of(session));

            assertThrows(SessionExpiredException.class, () -> controller.updateSubmission(30L, request));
        }
    }

    @Test
    void updateSubmission_shouldThrowUnauthorized_whenSessionOwnedByDifferentUser() {
        BehavioralSubmission existing = new BehavioralSubmission();
        existing.setId(30L);
        existing.setUser(user);

        session.setUser(otherUser);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(behavioralSubmissionService.getSubmissionById(30L)).thenReturn(Optional.of(existing));
            when(behavioralQuestionService.getQuestionById(10L)).thenReturn(Optional.of(question));
            when(assessmentSessionService.getSessionById(20L)).thenReturn(Optional.of(session));

            assertThrows(UnauthorizedException.class, () -> controller.updateSubmission(30L, request));
        }
    }

    @Test
    void deleteSubmission_shouldReturnNoContent_whenValid() {
        BehavioralSubmission existing = new BehavioralSubmission();
        existing.setId(30L);
        existing.setUser(user);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(behavioralSubmissionService.getSubmissionById(30L)).thenReturn(Optional.of(existing));

            ResponseEntity<Void> result = controller.deleteSubmission(30L);

            assertEquals(204, result.getStatusCode().value());
            verify(behavioralSubmissionService).deleteSubmission(30L);
        }
    }

    @Test
    void deleteSubmission_shouldThrowNotFound_whenMissing() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(behavioralSubmissionService.getSubmissionById(30L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> controller.deleteSubmission(30L));
        }
    }

    @Test
    void deleteSubmission_shouldThrowUnauthorized_whenNotOwner() {
        BehavioralSubmission existing = new BehavioralSubmission();
        existing.setId(30L);
        existing.setUser(otherUser);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(behavioralSubmissionService.getSubmissionById(30L)).thenReturn(Optional.of(existing));

            assertThrows(UnauthorizedException.class, () -> controller.deleteSubmission(30L));
        }
    }

    @Test
    void getMySubmissions_shouldThrowUnauthorized_whenNoAuthenticatedUser() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("");

            assertThrows(UnauthorizedException.class, () -> controller.getMySubmissions());
        }
    }

    @Test
    void getMySubmissions_shouldThrowNotFound_whenAuthenticatedUserMissingFromDatabase() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("missing@example.com");
            when(userService.getUserByEmail("missing@example.com")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> controller.getMySubmissions());
        }
    }
}