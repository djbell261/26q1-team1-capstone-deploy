package com.teamone.interviewprep.controller;

import com.teamone.interviewprep.dto.coding.CodingChallengeResponse;
import com.teamone.interviewprep.entity.CodingChallenge;
import com.teamone.interviewprep.mapper.CodingSubmissionMapper;
import com.teamone.interviewprep.service.ExternalChallengeImportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalChallengeControllerTest {

    @Mock
    private ExternalChallengeImportService externalChallengeImportService;

    @Mock
    private CodingSubmissionMapper codingSubmissionMapper;

    @InjectMocks
    private ExternalChallengeController controller;

    @Test
    void importChallenges_shouldReturnMappedList() {
        CodingChallenge challenge1 = new CodingChallenge();
        challenge1.setId(1L);

        CodingChallenge challenge2 = new CodingChallenge();
        challenge2.setId(2L);

        CodingChallengeResponse response1 = new CodingChallengeResponse();
        response1.setId(1L);

        CodingChallengeResponse response2 = new CodingChallengeResponse();
        response2.setId(2L);

        when(externalChallengeImportService.importChallenges()).thenReturn(List.of(challenge1, challenge2));
        when(codingSubmissionMapper.toChallengeResponse(challenge1)).thenReturn(response1);
        when(codingSubmissionMapper.toChallengeResponse(challenge2)).thenReturn(response2);

        ResponseEntity<List<CodingChallengeResponse>> result = controller.importChallenges();

        assertEquals(200, result.getStatusCode().value());
        assertEquals(2, result.getBody().size());
    }
}