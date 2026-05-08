package com.iacodereview.infrastructure.ai;

import com.iacodereview.domain.model.CodeAnalysis;
import com.iacodereview.domain.port.out.AiReviewPort;
import com.iacodereview.infrastructure.ai.config.AnthropicRestClientBuilder;
import com.iacodereview.infrastructure.ai.dto.AnalysisPayload;
import com.iacodereview.infrastructure.ai.dto.AnthropicResponse;
import com.iacodereview.infrastructure.exception.BadResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.Optional;

@Component
public class AnthropicAiAdapter implements AiReviewPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnthropicAiAdapter.class);

    private final AnthropicRestClientBuilder restClientBuilder;
    private final CodeAnalysisMapper codeAnalysisMapper;
    private final ObjectMapper objectMapper;
    private final RequestBodyBuilder requestBodyBuilder;

    public AnthropicAiAdapter(AnthropicRestClientBuilder restClientBuilder,
                              CodeAnalysisMapper codeAnalysisMapper,
                              ObjectMapper objectMapper,
                              RequestBodyBuilder requestBodyBuilder) {
        this.restClientBuilder = restClientBuilder;
        this.requestBodyBuilder = requestBodyBuilder;
        this.codeAnalysisMapper = codeAnalysisMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public CodeAnalysis analyzeCode(String code, String language) {
        var messageContent = "Langage: %s\nCode:\n%s".formatted(language, code);
        LOGGER.info("messageContent = {}", messageContent);
        final Map<String, Object> requestBody = requestBodyBuilder.buildRequestBody(messageContent);
        LOGGER.info("Body de la requete = {}", requestBody);
        AnthropicResponse response = restClientBuilder.restClient().post()
                .body(requestBody)
                .retrieve()
                .body(AnthropicResponse.class);
        LOGGER.info("Response = {}", response);
        return Optional.ofNullable(response)
                .map(AnthropicResponse::firstText)
                .map(this::parseAnalysis)
                .orElseThrow(() -> new BadResponseException("Reponse invalide"));
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
