package com.teamone.interviewprep.external.client;

import com.teamone.interviewprep.config.ExternalChallengeProperties;
import com.teamone.interviewprep.external.dto.ExternalChallengeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ExternalCodingApiClient {

    private final RestClient restClient;
    private final ExternalChallengeProperties properties;

    public List<ExternalChallengeDto> fetchChallenges() {
        if (!properties.isEnabled()) {
            return Collections.emptyList();
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> response = (List<Map<String, Object>>) restClient.get()
                .uri("https://leetcode-api-pied.vercel.app/problems")
                .retrieve()
                .body(List.class);

        if (response == null) {
            return Collections.emptyList();
        }

        System.out.println("RESPONSE SIZE = " + response.size());
        if (!response.isEmpty()) {
            System.out.println("FIRST ITEM = " + response.get(0));
        }

        return response.stream()
                .limit(20)
                .map(item -> {
                    String slug = getString(item, "titleSlug");

                    if (slug == null || slug.isBlank()) {
                        slug = getString(item, "slug");
                    }

                    if (slug == null || slug.isBlank()) {
                        slug = getString(item, "title");
                    }

                    String title = getString(item, "title");
                    if (title == null || title.isBlank()) {
                        title = "Untitled Challenge";
                    }

                    String description = getString(item, "content");
                    if (description == null || description.isBlank()) {
                        description = "No description";
                    }

                    Object difficultyObj = item.get("difficulty");
                    String difficulty = difficultyObj != null
                            ? difficultyObj.toString()
                            : "MEDIUM";

                    return ExternalChallengeDto.builder()
                            .externalId(slug)
                            .title(title)
                            .description(description)
                            .difficulty(difficulty)
                            .category("General")
                            .timeLimitMinutes(null)
                            .build();
                })
                .toList();
    }

    private String getString(Map<String, Object> item, String key) {
        Object value = item.get(key);
        return value != null ? value.toString() : null;
    }
}