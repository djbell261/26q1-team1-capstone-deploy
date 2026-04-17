package com.teamone.interviewprep.controller;

import com.teamone.interviewprep.dto.behavioral.BehavioralQuestionResponse;
import com.teamone.interviewprep.entity.BehavioralQuestion;
import com.teamone.interviewprep.mapper.BehavioralSubmissionMapper;
import com.teamone.interviewprep.service.BehavioralQuestionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BehavioralQuestionControllerTest {

    @Mock
    private BehavioralQuestionService behavioralQuestionService;

    @Mock
    private BehavioralSubmissionMapper behavioralSubmissionMapper;

    @InjectMocks
    private BehavioralQuestionController controller;

    @Test
    void createQuestion_shouldReturnResponse() {
        BehavioralQuestion question = new BehavioralQuestion();
        question.setId(1L);

        BehavioralQuestionResponse response = new BehavioralQuestionResponse();
        response.setId(1L);

        when(behavioralQuestionService.createQuestion(question)).thenReturn(question);
        when(behavioralSubmissionMapper.toQuestionResponse(question)).thenReturn(response);

        ResponseEntity<BehavioralQuestionResponse> result = controller.createQuestion(question);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(1L, result.getBody().getId());
    }

    @Test
    void getAllQuestions_shouldReturnMappedList() {
        BehavioralQuestion q1 = new BehavioralQuestion();
        q1.setId(1L);

        BehavioralQuestion q2 = new BehavioralQuestion();
        q2.setId(2L);

        BehavioralQuestionResponse r1 = new BehavioralQuestionResponse();
        r1.setId(1L);

        BehavioralQuestionResponse r2 = new BehavioralQuestionResponse();
        r2.setId(2L);

        when(behavioralQuestionService.getAllQuestions()).thenReturn(List.of(q1, q2));
        when(behavioralSubmissionMapper.toQuestionResponse(q1)).thenReturn(r1);
        when(behavioralSubmissionMapper.toQuestionResponse(q2)).thenReturn(r2);

        ResponseEntity<List<BehavioralQuestionResponse>> result = controller.getAllQuestions();

        assertEquals(200, result.getStatusCode().value());
        assertEquals(2, result.getBody().size());
    }

    @Test
    void getQuestionById_shouldReturnOk_whenFound() {
        BehavioralQuestion question = new BehavioralQuestion();
        question.setId(3L);

        BehavioralQuestionResponse response = new BehavioralQuestionResponse();
        response.setId(3L);

        when(behavioralQuestionService.getQuestionById(3L)).thenReturn(Optional.of(question));
        when(behavioralSubmissionMapper.toQuestionResponse(question)).thenReturn(response);

        ResponseEntity<BehavioralQuestionResponse> result = controller.getQuestionById(3L);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(3L, result.getBody().getId());
    }

    @Test
    void getQuestionById_shouldReturnNotFound_whenMissing() {
        when(behavioralQuestionService.getQuestionById(99L)).thenReturn(Optional.empty());

        ResponseEntity<BehavioralQuestionResponse> result = controller.getQuestionById(99L);

        assertEquals(404, result.getStatusCode().value());
        assertNull(result.getBody());
    }

    @Test
    void getQuestionsByCategory_shouldReturnMappedList() {
        BehavioralQuestion question = new BehavioralQuestion();
        question.setId(4L);

        BehavioralQuestionResponse response = new BehavioralQuestionResponse();
        response.setId(4L);

        when(behavioralQuestionService.getQuestionsByCategory("Leadership")).thenReturn(List.of(question));
        when(behavioralSubmissionMapper.toQuestionResponse(question)).thenReturn(response);

        ResponseEntity<List<BehavioralQuestionResponse>> result =
                controller.getQuestionsByCategory("Leadership");

        assertEquals(200, result.getStatusCode().value());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void getQuestionsByDifficulty_shouldReturnMappedList() {
        BehavioralQuestion question = new BehavioralQuestion();
        question.setId(5L);

        BehavioralQuestionResponse response = new BehavioralQuestionResponse();
        response.setId(5L);

        when(behavioralQuestionService.getQuestionsByDifficulty("EASY")).thenReturn(List.of(question));
        when(behavioralSubmissionMapper.toQuestionResponse(question)).thenReturn(response);

        ResponseEntity<List<BehavioralQuestionResponse>> result =
                controller.getQuestionsByDifficulty("EASY");

        assertEquals(200, result.getStatusCode().value());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void updateQuestion_shouldReturnUpdatedResponse() {
        BehavioralQuestion question = new BehavioralQuestion();
        question.setId(6L);

        BehavioralQuestionResponse response = new BehavioralQuestionResponse();
        response.setId(6L);

        when(behavioralQuestionService.updateQuestion(6L, question)).thenReturn(question);
        when(behavioralSubmissionMapper.toQuestionResponse(question)).thenReturn(response);

        ResponseEntity<BehavioralQuestionResponse> result = controller.updateQuestion(6L, question);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(6L, result.getBody().getId());
    }

    @Test
    void deleteQuestion_shouldReturnNoContent() {
        ResponseEntity<Void> result = controller.deleteQuestion(7L);

        assertEquals(204, result.getStatusCode().value());
        verify(behavioralQuestionService).deleteQuestion(7L);
    }
}