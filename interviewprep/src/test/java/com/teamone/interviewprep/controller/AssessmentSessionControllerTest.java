package com.teamone.interviewprep.controller;

import com.teamone.interviewprep.dto.session.AssessmentSessionRequest;
import com.teamone.interviewprep.dto.session.AssessmentSessionResponse;
import com.teamone.interviewprep.entity.AssessmentSession;
import com.teamone.interviewprep.entity.User;
import com.teamone.interviewprep.exception.ResourceNotFoundException;
import com.teamone.interviewprep.exception.SessionExpiredException;
import com.teamone.interviewprep.exception.UnauthorizedException;
import com.teamone.interviewprep.mapper.AssessmentSessionMapper;
import com.teamone.interviewprep.security.SecurityUtils;
import com.teamone.interviewprep.service.AssessmentSessionService;
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
class AssessmentSessionControllerTest {

    @Mock
    private AssessmentSessionService assessmentSessionService;

    @Mock
    private UserService userService;

    @Mock
    private AssessmentSessionMapper assessmentSessionMapper;

    @InjectMocks
    private AssessmentSessionController controller;

    private User user;
    private User otherUser;
    private AssessmentSessionRequest request;
    private AssessmentSession session;
    private AssessmentSessionResponse response;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setEmail("other@example.com");

        request = new AssessmentSessionRequest();

        session = new AssessmentSession();
        session.setId(10L);
        session.setUser(user);
        session.setExpiresAt(LocalDateTime.now().plusMinutes(30));

        response = new AssessmentSessionResponse();
        response.setId(10L);
    }

    @Test
    void createSession_shouldReturnResponse() {
        AssessmentSession mappedSession = new AssessmentSession();

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");

            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(assessmentSessionMapper.toEntity(request, user)).thenReturn(mappedSession);
            when(assessmentSessionService.createSession(mappedSession)).thenReturn(session);
            when(assessmentSessionMapper.toResponse(session)).thenReturn(response);

            ResponseEntity<AssessmentSessionResponse> result = controller.createSession(request);

            assertEquals(200, result.getStatusCode().value());
            assertNotNull(result.getBody());
            assertEquals(10L, result.getBody().getId());
        }
    }

    @Test
    void getAllSessions_shouldReturnMappedList() {
        AssessmentSession session2 = new AssessmentSession();
        session2.setId(11L);

        AssessmentSessionResponse response2 = new AssessmentSessionResponse();
        response2.setId(11L);

        when(assessmentSessionService.getAllSessions()).thenReturn(List.of(session, session2));
        when(assessmentSessionMapper.toResponse(session)).thenReturn(response);
        when(assessmentSessionMapper.toResponse(session2)).thenReturn(response2);

        ResponseEntity<List<AssessmentSessionResponse>> result = controller.getAllSessions();

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().size());
        assertEquals(10L, result.getBody().get(0).getId());
        assertEquals(11L, result.getBody().get(1).getId());
    }

    @Test
    void getMySessions_shouldReturnMappedList() {
        AssessmentSession session2 = new AssessmentSession();
        session2.setId(11L);
        session2.setUser(user);

        AssessmentSessionResponse response2 = new AssessmentSessionResponse();
        response2.setId(11L);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");

            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(assessmentSessionService.getSessionsByUserId(1L)).thenReturn(List.of(session, session2));
            when(assessmentSessionMapper.toResponse(session)).thenReturn(response);
            when(assessmentSessionMapper.toResponse(session2)).thenReturn(response2);

            ResponseEntity<List<AssessmentSessionResponse>> result = controller.getMySessions();

            assertEquals(200, result.getStatusCode().value());
            assertEquals(2, result.getBody().size());
        }
    }

    @Test
    void getSessionById_shouldReturnResponse_whenOwnedByCurrentUser() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");

            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(assessmentSessionService.getSessionById(10L)).thenReturn(Optional.of(session));
            when(assessmentSessionMapper.toResponse(session)).thenReturn(response);

            ResponseEntity<AssessmentSessionResponse> result = controller.getSessionById(10L);

            assertEquals(200, result.getStatusCode().value());
            assertNotNull(result.getBody());
            assertEquals(10L, result.getBody().getId());
        }
    }

    @Test
    void getSessionById_shouldReturnNotFound_whenMissing() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");

            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(assessmentSessionService.getSessionById(99L)).thenReturn(Optional.empty());

            ResponseEntity<AssessmentSessionResponse> result = controller.getSessionById(99L);

            assertEquals(404, result.getStatusCode().value());
            assertNull(result.getBody());
        }
    }

    @Test
    void getSessionById_shouldThrowUnauthorized_whenNotOwner() {
        session.setUser(otherUser);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");

            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(assessmentSessionService.getSessionById(10L)).thenReturn(Optional.of(session));

            assertThrows(UnauthorizedException.class, () -> controller.getSessionById(10L));
        }
    }

    @Test
    void updateSession_shouldReturnUpdatedResponse_whenValid() {
        AssessmentSession updatedMapped = new AssessmentSession();
        AssessmentSession saved = new AssessmentSession();
        saved.setId(10L);
        saved.setUser(user);

        AssessmentSessionResponse savedResponse = new AssessmentSessionResponse();
        savedResponse.setId(10L);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");

            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(assessmentSessionService.getSessionById(10L)).thenReturn(Optional.of(session));
            when(assessmentSessionMapper.toEntity(request, user)).thenReturn(updatedMapped);
            when(assessmentSessionService.updateSession(10L, updatedMapped)).thenReturn(saved);
            when(assessmentSessionMapper.toResponse(saved)).thenReturn(savedResponse);

            ResponseEntity<AssessmentSessionResponse> result = controller.updateSession(10L, request);

            assertEquals(200, result.getStatusCode().value());
            assertNotNull(result.getBody());
            assertEquals(10L, result.getBody().getId());
        }
    }

    @Test
    void updateSession_shouldThrowNotFound_whenSessionMissing() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");

            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(assessmentSessionService.getSessionById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> controller.updateSession(99L, request));
        }
    }

    @Test
    void updateSession_shouldThrowUnauthorized_whenNotOwner() {
        session.setUser(otherUser);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");

            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(assessmentSessionService.getSessionById(10L)).thenReturn(Optional.of(session));

            assertThrows(UnauthorizedException.class, () -> controller.updateSession(10L, request));
        }
    }

    @Test
    void updateSession_shouldThrowExpired_whenSessionExpired() {
        session.setExpiresAt(LocalDateTime.now().minusMinutes(5));

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");

            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(assessmentSessionService.getSessionById(10L)).thenReturn(Optional.of(session));

            assertThrows(SessionExpiredException.class, () -> controller.updateSession(10L, request));
        }
    }

    @Test
    void deleteSession_shouldReturnNoContent_whenValid() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");

            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(assessmentSessionService.getSessionById(10L)).thenReturn(Optional.of(session));

            ResponseEntity<Void> result = controller.deleteSession(10L);

            assertEquals(204, result.getStatusCode().value());
            verify(assessmentSessionService).deleteSession(10L);
        }
    }

    @Test
    void deleteSession_shouldThrowNotFound_whenMissing() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");

            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(assessmentSessionService.getSessionById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> controller.deleteSession(99L));
        }
    }

    @Test
    void deleteSession_shouldThrowUnauthorized_whenNotOwner() {
        session.setUser(otherUser);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");

            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(assessmentSessionService.getSessionById(10L)).thenReturn(Optional.of(session));

            assertThrows(UnauthorizedException.class, () -> controller.deleteSession(10L));
        }
    }

    @Test
    void createSession_shouldThrowUnauthorized_whenNoAuthenticatedUser() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("");

            assertThrows(UnauthorizedException.class, () -> controller.createSession(request));
        }
    }

    @Test
    void createSession_shouldThrowResourceNotFound_whenAuthenticatedUserMissingFromDatabase() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("missing@example.com");
            when(userService.getUserByEmail("missing@example.com")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> controller.createSession(request));
        }
    }
}