package com.teamone.interviewprep.controller;

import com.teamone.interviewprep.dto.coding.CodingChallengeResponse;
import com.teamone.interviewprep.entity.CodingChallenge;
import com.teamone.interviewprep.mapper.CodingSubmissionMapper;
import com.teamone.interviewprep.service.CodingChallengeService;
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
class CodingChallengeControllerTest {

    @Mock
    private CodingChallengeService codingChallengeService;

    @Mock
    private CodingSubmissionMapper codingSubmissionMapper;

    @InjectMocks
    private CodingChallengeController controller;

    @Test
    void createChallenge_shouldReturnResponse() {
        CodingChallenge challenge = new CodingChallenge();
        challenge.setId(1L);

        CodingChallengeResponse response = new CodingChallengeResponse();
        response.setId(1L);

        when(codingChallengeService.createChallenge(challenge)).thenReturn(challenge);
        when(codingSubmissionMapper.toChallengeResponse(challenge)).thenReturn(response);

        ResponseEntity<CodingChallengeResponse> result = controller.createChallenge(challenge);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(1L, result.getBody().getId());
    }

    @Test
    void getAllChallenges_shouldReturnMappedList() {
        CodingChallenge c1 = new CodingChallenge();
        c1.setId(1L);

        CodingChallenge c2 = new CodingChallenge();
        c2.setId(2L);

        CodingChallengeResponse r1 = new CodingChallengeResponse();
        r1.setId(1L);

        CodingChallengeResponse r2 = new CodingChallengeResponse();
        r2.setId(2L);

        when(codingChallengeService.getAllChallenges()).thenReturn(List.of(c1, c2));
        when(codingSubmissionMapper.toChallengeResponse(c1)).thenReturn(r1);
        when(codingSubmissionMapper.toChallengeResponse(c2)).thenReturn(r2);

        ResponseEntity<List<CodingChallengeResponse>> result = controller.getAllChallenges();

        assertEquals(200, result.getStatusCode().value());
        assertEquals(2, result.getBody().size());
    }

    @Test
    void getChallengeById_shouldReturnOk_whenFound() {
        CodingChallenge challenge = new CodingChallenge();
        challenge.setId(3L);

        CodingChallengeResponse response = new CodingChallengeResponse();
        response.setId(3L);

        when(codingChallengeService.getChallengeById(3L)).thenReturn(Optional.of(challenge));
        when(codingSubmissionMapper.toChallengeResponse(challenge)).thenReturn(response);

        ResponseEntity<CodingChallengeResponse> result = controller.getChallengeById(3L);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(3L, result.getBody().getId());
    }

    @Test
    void getChallengeById_shouldReturnNotFound_whenMissing() {
        when(codingChallengeService.getChallengeById(99L)).thenReturn(Optional.empty());

        ResponseEntity<CodingChallengeResponse> result = controller.getChallengeById(99L);

        assertEquals(404, result.getStatusCode().value());
        assertNull(result.getBody());
    }

    @Test
    void getChallengesByDifficulty_shouldReturnMappedList() {
        CodingChallenge challenge = new CodingChallenge();
        challenge.setId(4L);

        CodingChallengeResponse response = new CodingChallengeResponse();
        response.setId(4L);

        when(codingChallengeService.getChallengesByDifficulty("EASY")).thenReturn(List.of(challenge));
        when(codingSubmissionMapper.toChallengeResponse(challenge)).thenReturn(response);

        ResponseEntity<List<CodingChallengeResponse>> result =
                controller.getChallengesByDifficulty("EASY");

        assertEquals(200, result.getStatusCode().value());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void getChallengesByCategory_shouldReturnMappedList() {
        CodingChallenge challenge = new CodingChallenge();
        challenge.setId(5L);

        CodingChallengeResponse response = new CodingChallengeResponse();
        response.setId(5L);

        when(codingChallengeService.getChallengesByCategory("Arrays")).thenReturn(List.of(challenge));
        when(codingSubmissionMapper.toChallengeResponse(challenge)).thenReturn(response);

        ResponseEntity<List<CodingChallengeResponse>> result =
                controller.getChallengesByCategory("Arrays");

        assertEquals(200, result.getStatusCode().value());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void updateChallenge_shouldReturnUpdatedResponse() {
        CodingChallenge challenge = new CodingChallenge();
        challenge.setId(6L);

        CodingChallengeResponse response = new CodingChallengeResponse();
        response.setId(6L);

        when(codingChallengeService.updateChallenge(6L, challenge)).thenReturn(challenge);
        when(codingSubmissionMapper.toChallengeResponse(challenge)).thenReturn(response);

        ResponseEntity<CodingChallengeResponse> result = controller.updateChallenge(6L, challenge);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(6L, result.getBody().getId());
    }

    @Test
    void deleteChallenge_shouldReturnNoContent() {
        ResponseEntity<Void> result = controller.deleteChallenge(7L);

        assertEquals(204, result.getStatusCode().value());
        verify(codingChallengeService).deleteChallenge(7L);
    }
}