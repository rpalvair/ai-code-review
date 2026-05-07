package com.iacodereview.infrastructure.ai.config;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AnthropicRestClientBuilder {

    private final RestClient restClient;

    public AnthropicRestClientBuilder(AnthropicProperties anthropicProperties) {
        this.restClient = RestClient.builder()
                .baseUrl(anthropicProperties.apiUrl())
                .defaultHeader("x-api-key", anthropicProperties.apiKey())
                .defaultHeader("anthropic-version", anthropicProperties.apiVersion())
                .defaultHeader("content-type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public RestClient get() {
        return restClient;
    }
}
