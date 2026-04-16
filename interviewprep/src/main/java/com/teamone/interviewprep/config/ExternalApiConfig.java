package com.teamone.interviewprep.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(ExternalChallengeProperties.class)
public class ExternalApiConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.builder().build();
    }
}