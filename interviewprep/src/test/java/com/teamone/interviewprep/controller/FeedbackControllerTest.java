package com.teamone.interviewprep.controller;

import com.teamone.interviewprep.dto.feedback.FeedbackResponse;
import com.teamone.interviewprep.entity.BehavioralSubmission;
import com.teamone.interviewprep.entity.CodingSubmission;
import com.teamone.interviewprep.entity.Feedback;
import com.teamone.interviewprep.entity.User;
import com.teamone.interviewprep.exception.ResourceNotFoundException;
import com.teamone.interviewprep.exception.UnauthorizedException;
import com.teamone.interviewprep.mapper.FeedbackMapper;
import com.teamone.interviewprep.security.SecurityUtils;
import com.teamone.interviewprep.service.BehavioralSubmissionService;
import com.teamone.interviewprep.service.CodingSubmissionService;
import com.teamone.interviewprep.service.FeedbackService;
import com.teamone.interviewprep.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackControllerTest {

    @Mock
    private FeedbackService feedbackService;

    @Mock
    private FeedbackMapper feedbackMapper;

    @Mock
    private UserService userService;

    @Mock
    private CodingSubmissionService codingSubmissionService;

    @Mock
    private BehavioralSubmissionService behavioralSubmissionService;

    @InjectMocks
    private FeedbackController controller;

    private User user;
    private User otherUser;
    private Feedback feedback;
    private FeedbackResponse response;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setEmail("other@example.com");

        feedback = new Feedback();
        feedback.setId(10L);

        response = new FeedbackResponse();
        response.setId(10L);
    }

    @Test
    void createFeedback_shouldReturnResponse() {
        when(feedbackService.createFeedback(feedback)).thenReturn(feedback);
        when(feedbackMapper.toResponse(feedback)).thenReturn(response);

        ResponseEntity<FeedbackResponse> result = controller.createFeedback(feedback);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(10L, result.getBody().getId());
    }

    @Test
    void getAllFeedback_shouldReturnMappedList() {
        Feedback feedback2 = new Feedback();
        feedback2.setId(11L);

        FeedbackResponse response2 = new FeedbackResponse();
        response2.setId(11L);

        when(feedbackService.getAllFeedback()).thenReturn(List.of(feedback, feedback2));
        when(feedbackMapper.toResponse(feedback)).thenReturn(response);
        when(feedbackMapper.toResponse(feedback2)).thenReturn(response2);

        ResponseEntity<List<FeedbackResponse>> result = controller.getAllFeedback();

        assertEquals(200, result.getStatusCode().value());
        assertEquals(2, result.getBody().size());
    }

    @Test
    void getFeedbackById_shouldReturnResponse_whenCodingOwnerMatches() {
        CodingSubmission submission = new CodingSubmission();
        submission.setUser(user);
        feedback.setCodingSubmission(submission);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(feedbackService.getFeedbackById(10L)).thenReturn(Optional.of(feedback));
            when(feedbackMapper.toResponse(feedback)).thenReturn(response);

            ResponseEntity<FeedbackResponse> result = controller.getFeedbackById(10L);

            assertEquals(200, result.getStatusCode().value());
            assertEquals(10L, result.getBody().getId());
        }
    }

    @Test
    void getFeedbackById_shouldReturnResponse_whenBehavioralOwnerMatches() {
        BehavioralSubmission submission = new BehavioralSubmission();
        submission.setUser(user);
        feedback.setBehavioralSubmission(submission);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(feedbackService.getFeedbackById(10L)).thenReturn(Optional.of(feedback));
            when(feedbackMapper.toResponse(feedback)).thenReturn(response);

            ResponseEntity<FeedbackResponse> result = controller.getFeedbackById(10L);

            assertEquals(200, result.getStatusCode().value());
            assertEquals(10L, result.getBody().getId());
        }
    }

    @Test
    void getFeedbackById_shouldThrowNotFound_whenMissing() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(feedbackService.getFeedbackById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> controller.getFeedbackById(99L));
        }
    }

    @Test
    void getFeedbackById_shouldThrowUnauthorized_whenNotOwned() {
        CodingSubmission submission = new CodingSubmission();
        submission.setUser(otherUser);
        feedback.setCodingSubmission(submission);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(feedbackService.getFeedbackById(10L)).thenReturn(Optional.of(feedback));

            assertThrows(UnauthorizedException.class, () -> controller.getFeedbackById(10L));
        }
    }

    @Test
    void getFeedbackByCodingSubmissionId_shouldReturnResponse_whenOwned() {
        CodingSubmission submission = new CodingSubmission();
        submission.setId(5L);
        submission.setUser(user);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(codingSubmissionService.getSubmissionById(5L)).thenReturn(Optional.of(submission));
            when(feedbackService.getFeedbackByCodingSubmissionId(5L)).thenReturn(Optional.of(feedback));
            when(feedbackMapper.toResponse(feedback)).thenReturn(response);

            ResponseEntity<FeedbackResponse> result = controller.getFeedbackByCodingSubmissionId(5L);

            assertEquals(200, result.getStatusCode().value());
            assertEquals(10L, result.getBody().getId());
        }
    }

    @Test
    void getFeedbackByCodingSubmissionId_shouldReturnNotFound_whenFeedbackMissing() {
        CodingSubmission submission = new CodingSubmission();
        submission.setId(5L);
        submission.setUser(user);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(codingSubmissionService.getSubmissionById(5L)).thenReturn(Optional.of(submission));
            when(feedbackService.getFeedbackByCodingSubmissionId(5L)).thenReturn(Optional.empty());

            ResponseEntity<FeedbackResponse> result = controller.getFeedbackByCodingSubmissionId(5L);

            assertEquals(404, result.getStatusCode().value());
        }
    }

    @Test
    void getFeedbackByCodingSubmissionId_shouldThrowNotFound_whenSubmissionMissing() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(codingSubmissionService.getSubmissionById(5L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> controller.getFeedbackByCodingSubmissionId(5L));
        }
    }

    @Test
    void getFeedbackByCodingSubmissionId_shouldThrowUnauthorized_whenNotOwned() {
        CodingSubmission submission = new CodingSubmission();
        submission.setId(5L);
        submission.setUser(otherUser);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(codingSubmissionService.getSubmissionById(5L)).thenReturn(Optional.of(submission));

            assertThrows(UnauthorizedException.class, () -> controller.getFeedbackByCodingSubmissionId(5L));
        }
    }

    @Test
    void getFeedbackByBehavioralSubmissionId_shouldReturnResponse_whenOwned() {
        BehavioralSubmission submission = new BehavioralSubmission();
        submission.setId(7L);
        submission.setUser(user);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(behavioralSubmissionService.getSubmissionById(7L)).thenReturn(Optional.of(submission));
            when(feedbackService.getFeedbackByBehavioralSubmissionId(7L)).thenReturn(Optional.of(feedback));
            when(feedbackMapper.toResponse(feedback)).thenReturn(response);

            ResponseEntity<FeedbackResponse> result = controller.getFeedbackByBehavioralSubmissionId(7L);

            assertEquals(200, result.getStatusCode().value());
            assertEquals(10L, result.getBody().getId());
        }
    }

    @Test
    void getFeedbackByBehavioralSubmissionId_shouldReturnNotFound_whenFeedbackMissing() {
        BehavioralSubmission submission = new BehavioralSubmission();
        submission.setId(7L);
        submission.setUser(user);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(behavioralSubmissionService.getSubmissionById(7L)).thenReturn(Optional.of(submission));
            when(feedbackService.getFeedbackByBehavioralSubmissionId(7L)).thenReturn(Optional.empty());

            ResponseEntity<FeedbackResponse> result = controller.getFeedbackByBehavioralSubmissionId(7L);

            assertEquals(404, result.getStatusCode().value());
        }
    }

    @Test
    void getFeedbackByBehavioralSubmissionId_shouldThrowNotFound_whenSubmissionMissing() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(behavioralSubmissionService.getSubmissionById(7L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> controller.getFeedbackByBehavioralSubmissionId(7L));
        }
    }

    @Test
    void getFeedbackByBehavioralSubmissionId_shouldThrowUnauthorized_whenNotOwned() {
        BehavioralSubmission submission = new BehavioralSubmission();
        submission.setId(7L);
        submission.setUser(otherUser);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(behavioralSubmissionService.getSubmissionById(7L)).thenReturn(Optional.of(submission));

            assertThrows(UnauthorizedException.class, () -> controller.getFeedbackByBehavioralSubmissionId(7L));
        }
    }

    @Test
    void updateFeedback_shouldReturnUpdatedResponse() {
        when(feedbackService.updateFeedback(10L, feedback)).thenReturn(feedback);
        when(feedbackMapper.toResponse(feedback)).thenReturn(response);

        ResponseEntity<FeedbackResponse> result = controller.updateFeedback(10L, feedback);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(10L, result.getBody().getId());
    }

    @Test
    void deleteFeedback_shouldReturnNoContent() {
        ResponseEntity<Void> result = controller.deleteFeedback(10L);

        assertEquals(204, result.getStatusCode().value());
        verify(feedbackService).deleteFeedback(10L);
    }

    @Test
    void getFeedbackById_shouldThrowUnauthorized_whenNoAuthenticatedUser() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("");

            assertThrows(UnauthorizedException.class, () -> controller.getFeedbackById(10L));
        }
    }

    @Test
    void getFeedbackById_shouldThrowNotFound_whenAuthenticatedUserMissingFromDatabase() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("missing@example.com");
            when(userService.getUserByEmail("missing@example.com")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> controller.getFeedbackById(10L));
        }
    }
}