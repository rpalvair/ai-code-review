package com.iacodereview.infrastructure.ai.anthropic.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("anthropic")
public record AnthropicProperties(
        String apiKey,
        @DefaultValue("https://api.anthropic.com/v1/messages") String apiUrl,
        @DefaultValue("claude-sonnet-4-6") String model,
        @DefaultValue("2023-06-01") String apiVersion,
        @DefaultValue("1024") int maxTokens
) {}
