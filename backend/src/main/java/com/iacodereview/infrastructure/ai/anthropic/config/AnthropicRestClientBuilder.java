package com.iacodereview.infrastructure.ai.anthropic.config;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AnthropicRestClientBuilder {

    private final RestClient restClient;

    public AnthropicRestClientBuilder(AnthropicProperties properties,
                                      RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl(properties.apiUrl())
                .defaultHeader("x-api-key", properties.apiKey())
                .defaultHeader("anthropic-version", properties.apiVersion())
                .defaultHeader("content-type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public RestClient restClient() {
        return restClient;
    }
}
