package com.iacodereview.infrastructure.ai;

import com.iacodereview.infrastructure.ai.config.AnthropicProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
public class RequestBodyBuilder {

    private final AnthropicProperties anthropicProperties;
    private final String systemPrompt;

    public RequestBodyBuilder(AnthropicProperties anthropicProperties,
                              @Value("classpath:prompts/system-prompt.txt") Resource systemPromptResource) throws IOException {
        this.anthropicProperties = anthropicProperties;
        this.systemPrompt = systemPromptResource.getContentAsString(StandardCharsets.UTF_8);
    }

    public  Map<String, Object> buildRequestBody(String userMessage) {
        return Map.of(
                "model", anthropicProperties.model(),
                "max_tokens", anthropicProperties.maxTokens(),
                "system", systemPrompt,
                "messages", List.of(Map.of("role", "user", "content", userMessage))
        );
    }
}
