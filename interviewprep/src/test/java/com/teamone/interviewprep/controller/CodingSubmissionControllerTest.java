package com.teamone.interviewprep.controller;
import com.teamone.interviewprep.dto.coding.CodingSubmissionRequest;
import com.teamone.interviewprep.dto.coding.CodingSubmissionResponse;
import com.teamone.interviewprep.entity.AssessmentSession;
import com.teamone.interviewprep.entity.CodingChallenge;
import com.teamone.interviewprep.entity.CodingSubmission;
import com.teamone.interviewprep.entity.User;
import com.teamone.interviewprep.enums.SubmissionStatus;
import com.teamone.interviewprep.exception.ResourceNotFoundException;
import com.teamone.interviewprep.exception.SessionExpiredException;
import com.teamone.interviewprep.exception.UnauthorizedException;
import com.teamone.interviewprep.mapper.CodingSubmissionMapper;
import com.teamone.interviewprep.security.SecurityUtils;
import com.teamone.interviewprep.service.AiService;
import com.teamone.interviewprep.service.AssessmentSessionService;
import com.teamone.interviewprep.service.CodingChallengeService;
import com.teamone.interviewprep.service.CodingSubmissionService;
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
class CodingSubmissionControllerTest {
    @Mock
    private CodingSubmissionService codingSubmissionService;
    @Mock
    private UserService userService;
    @Mock
    private CodingChallengeService codingChallengeService;
    @Mock
    private AssessmentSessionService assessmentSessionService;

    @Mock
    private CodingSubmissionMapper codingSubmissionMapper;
    @Mock
    private AiService aiService;
    @InjectMocks
    private CodingSubmissionController codingSubmissionController;
    private User user;
    private User otherUser;
    private CodingChallenge challenge;
    private AssessmentSession session;
    private CodingSubmissionRequest request;
    private CodingSubmission submission;
    private CodingSubmissionResponse response;
    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setEmail("other@example.com");
        challenge = new CodingChallenge();
        challenge.setId(10L);
        session = new AssessmentSession();
        session.setId(20L);
        session.setUser(user);
        session.setExpiresAt(LocalDateTime.now().plusMinutes(30));
        request = new CodingSubmissionRequest();
        request.setChallengeId(10L);
        request.setSessionId(20L);
        submission = new CodingSubmission();
        submission.setId(30L);
        submission.setUser(user);
        response = new CodingSubmissionResponse();
        response.setId(30L);
    }
    @Test
    void createSubmission_shouldReturnSubmissionResponse_whenValid() {
        CodingSubmission mappedSubmission = new CodingSubmission();

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(codingChallengeService.getChallengeById(10L)).thenReturn(Optional.of(challenge));
            when(assessmentSessionService.getSessionById(20L)).thenReturn(Optional.of(session));
            when(codingSubmissionMapper.toEntity(request, user, challenge,
                    session)).thenReturn(mappedSubmission);
            when(codingSubmissionService.createSubmission(mappedSubmission)).thenReturn(submission);
            when(codingSubmissionMapper.toResponse(submission)).thenReturn(response);
            ResponseEntity<CodingSubmissionResponse> result =
                    codingSubmissionController.createSubmission(request);
            assertEquals(200, result.getStatusCode().value());
            assertNotNull(result.getBody());
            assertEquals(30L, result.getBody().getId());
            assertEquals(SubmissionStatus.SUBMITTED, mappedSubmission.getStatus());
            assertNotNull(mappedSubmission.getSubmittedAt());
            verify(codingSubmissionService).createSubmission(mappedSubmission);
            verify(aiService).generateCodingFeedback(30L);
            verify(codingSubmissionMapper).toResponse(submission);
        }
    }
    @Test
    void createSubmission_shouldThrowResourceNotFound_whenChallengeMissing() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(codingChallengeService.getChallengeById(10L)).thenReturn(Optional.empty());
            ResourceNotFoundException ex = assertThrows(
                    ResourceNotFoundException.class,
                    () -> codingSubmissionController.createSubmission(request)
            );
            assertTrue(ex.getMessage().contains("Challenge not found"));
            verify(assessmentSessionService, never()).getSessionById(anyLong());
            verify(codingSubmissionService, never()).createSubmission(any());
            verify(aiService, never()).generateCodingFeedback(anyLong());
        }
    }
    @Test
    void createSubmission_shouldThrowResourceNotFound_whenSessionMissing() {

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(codingChallengeService.getChallengeById(10L)).thenReturn(Optional.of(challenge));
            when(assessmentSessionService.getSessionById(20L)).thenReturn(Optional.empty());
            ResourceNotFoundException ex = assertThrows(
                    ResourceNotFoundException.class,
                    () -> codingSubmissionController.createSubmission(request)
            );
            assertTrue(ex.getMessage().contains("Session not found"));
            verify(codingSubmissionService, never()).createSubmission(any());
            verify(aiService, never()).generateCodingFeedback(anyLong());
        }
    }
    @Test
    void createSubmission_shouldThrowSessionExpiredException_whenSessionExpired() {
        session.setExpiresAt(LocalDateTime.now().minusMinutes(5));
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(codingChallengeService.getChallengeById(10L)).thenReturn(Optional.of(challenge));
            when(assessmentSessionService.getSessionById(20L)).thenReturn(Optional.of(session));
            assertThrows(
                    SessionExpiredException.class,
                    () -> codingSubmissionController.createSubmission(request)
            );
            verify(codingSubmissionService, never()).createSubmission(any());
            verify(aiService, never()).generateCodingFeedback(anyLong());
        }
    }
    @Test
    void createSubmission_shouldThrowUnauthorizedException_whenSessionBelongsToAnotherUser() {
        session.setUser(otherUser);
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(codingChallengeService.getChallengeById(10L)).thenReturn(Optional.of(challenge));
            when(assessmentSessionService.getSessionById(20L)).thenReturn(Optional.of(session));

            UnauthorizedException ex = assertThrows(
                    UnauthorizedException.class,
                    () -> codingSubmissionController.createSubmission(request)
            );
            assertTrue(ex.getMessage().contains("Session does not belong to you"));
            verify(codingSubmissionService, never()).createSubmission(any());
            verify(aiService, never()).generateCodingFeedback(anyLong());
        }
    }
    @Test
    void getMySubmissions_shouldReturnOnlyMappedResponses() {
        CodingSubmission submission1 = new CodingSubmission();
        submission1.setId(101L);
        submission1.setUser(user);
        CodingSubmission submission2 = new CodingSubmission();
        submission2.setId(102L);
        submission2.setUser(user);
        CodingSubmissionResponse response1 = new CodingSubmissionResponse();
        response1.setId(101L);
        CodingSubmissionResponse response2 = new CodingSubmissionResponse();
        response2.setId(102L);
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(codingSubmissionService.getSubmissionsByUserId(1L)).thenReturn(List.of(submission1,
                    submission2));
            when(codingSubmissionMapper.toResponse(submission1)).thenReturn(response1);
            when(codingSubmissionMapper.toResponse(submission2)).thenReturn(response2);
            ResponseEntity<List<CodingSubmissionResponse>> result =
                    codingSubmissionController.getMySubmissions();
            assertEquals(200, result.getStatusCode().value());
            assertNotNull(result.getBody());
            assertEquals(2, result.getBody().size());
            assertEquals(101L, result.getBody().get(0).getId());
            assertEquals(102L, result.getBody().get(1).getId());
        }
    }
    @Test
    void getSubmissionById_shouldReturnResponse_whenOwnedByCurrentUser() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {

            mockedSecurity.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(codingSubmissionService.getSubmissionById(30L)).thenReturn(Optional.of(submission));
            when(codingSubmissionMapper.toResponse(submission)).thenReturn(response);
            ResponseEntity<CodingSubmissionResponse> result =
                    codingSubmissionController.getSubmissionById(30L);
            assertEquals(200, result.getStatusCode().value());
            assertNotNull(result.getBody());
            assertEquals(30L, result.getBody().getId());
        }
    }
    @Test
    void getSubmissionById_shouldThrowUnauthorizedException_whenNotOwnedByCurrentUser() {
        submission.setUser(otherUser);
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(codingSubmissionService.getSubmissionById(30L)).thenReturn(Optional.of(submission));
            assertThrows(
                    UnauthorizedException.class,
                    () -> codingSubmissionController.getSubmissionById(30L)
            );
            verify(codingSubmissionMapper, never()).toResponse(any());
        }
    }
}