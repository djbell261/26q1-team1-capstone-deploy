package com.teamone.interviewprep.controller;
import com.teamone.interviewprep.dto.session.AssessmentSessionRequest;
import com.teamone.interviewprep.dto.session.AssessmentSessionResponse;
import com.teamone.interviewprep.entity.AssessmentSession;
import com.teamone.interviewprep.entity.User;
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
    @Mock private AssessmentSessionService assessmentSessionService;
    @Mock private UserService userService;
    @Mock private AssessmentSessionMapper assessmentSessionMapper;
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
        request = new AssessmentSessionRequest();
        session = new AssessmentSession();
        session.setId(10L);
        session.setUser(user);
        session.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        response = new AssessmentSessionResponse();
        response.setId(10L);
    }
    @Test
    void createSession_shouldReturnResponse() {
        AssessmentSession mapped = new AssessmentSession();
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(assessmentSessionMapper.toEntity(request, user)).thenReturn(mapped);
            when(assessmentSessionService.createSession(mapped)).thenReturn(session);
            when(assessmentSessionMapper.toResponse(session)).thenReturn(response);
            ResponseEntity<AssessmentSessionResponse> result = controller.createSession(request);
            assertEquals(200, result.getStatusCode().value());
            assertEquals(10L, result.getBody().getId());
        }
    }
    @Test
    void getMySessions_shouldReturnMappedList() {
        AssessmentSession s2 = new AssessmentSession();
        s2.setId(11L);
        s2.setUser(user);
        AssessmentSessionResponse r2 = new AssessmentSessionResponse();
        r2.setId(11L);
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(assessmentSessionService.getSessionsByUserId(1L)).thenReturn(List.of(session, s2));
            when(assessmentSessionMapper.toResponse(session)).thenReturn(response);
            when(assessmentSessionMapper.toResponse(s2)).thenReturn(r2);

            ResponseEntity<List<AssessmentSessionResponse>> result = controller.getMySessions();
            assertEquals(200, result.getStatusCode().value());
            assertEquals(2, result.getBody().size());
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
    void updateSession_shouldThrowExpired_whenExpired() {
        session.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(assessmentSessionService.getSessionById(10L)).thenReturn(Optional.of(session));
            assertThrows(SessionExpiredException.class, () -> controller.updateSession(10L, request));
        }
    }
    @Test
    void deleteSession_shouldReturnNoContent() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@example.com");
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(assessmentSessionService.getSessionById(10L)).thenReturn(Optional.of(session));
            ResponseEntity<Void> result = controller.deleteSession(10L);
            assertEquals(204, result.getStatusCode().value());
            verify(assessmentSessionService).deleteSession(10L);
        }
    }
}