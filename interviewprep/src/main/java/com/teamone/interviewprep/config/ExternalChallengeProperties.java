package com.teamone.interviewprep.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "external.challenge.api")
public class ExternalChallengeProperties {
    private String baseUrl;
    private String importPath;
    private String apiKey;
    private String apiHost;
    private boolean enabled = false;
}