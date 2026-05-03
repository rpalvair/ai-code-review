package com.iacodereview.infrastructure.ai;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.iacodereview.domain.model.CodeAnalysis;
import com.iacodereview.domain.port.out.AiReviewPort;
import com.iacodereview.infrastructure.ai.config.AnthropicProperties;
import com.iacodereview.infrastructure.ai.config.AnthropicRestClientBuilder;
import com.iacodereview.infrastructure.ai.dto.AnalysisPayload;
import com.iacodereview.infrastructure.ai.dto.AnthropicResponse;

import tools.jackson.databind.ObjectMapper;

@Component
public class AnthropicAiAdapter implements AiReviewPort {

    private final AnthropicRestClientBuilder restClientBuilder;
    private final AnthropicProperties anthropicProperties;
    private final CodeAnalysisMapper codeAnalysisMapper;
    private final ObjectMapper objectMapper;
    private final String systemPrompt;

    public AnthropicAiAdapter(
            AnthropicRestClientBuilder restClientBuilder,
            AnthropicProperties anthropicProperties,
            CodeAnalysisMapper feedbackMapper,
            @Value("classpath:prompts/system-prompt.txt") Resource systemPromptResource,
            ObjectMapper objectMapper
    ) throws IOException {
        this.restClientBuilder = restClientBuilder;
        this.anthropicProperties = anthropicProperties;
        this.codeAnalysisMapper = feedbackMapper;
        this.objectMapper = objectMapper;
        this.systemPrompt = systemPromptResource.getContentAsString(StandardCharsets.UTF_8);
    }

    @Override
    public CodeAnalysis analyzeCode(String code, String language) {
        String userMessage = "Langage : " + language + "\n\nCode :\n" + code;

        Map<String, Object> requestBody = Map.of(
                "model", anthropicProperties.model(),
                "max_tokens", anthropicProperties.maxTokens(),
                "system", systemPrompt,
                "messages", List.of(Map.of("role", "user", "content", userMessage))
        );

        AnthropicResponse response = restClientBuilder.get().post()
                .body(requestBody)
                .retrieve()
                .body(AnthropicResponse.class);

        return parseAnalysis(response.firstText());
    }

    private CodeAnalysis parseAnalysis(String json) {
        try {
            AnalysisPayload dto = objectMapper.readValue(json, AnalysisPayload.class);
            return codeAnalysisMapper.toDomain(dto);
        } catch (Exception e) {
            throw new IllegalStateException("Impossible de parser la réponse Claude : " + json, e);
        }
    }
}
