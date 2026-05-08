package com.iacodereview.infrastructure.ai;

import com.iacodereview.domain.model.CodeAnalysis;
import com.iacodereview.infrastructure.ai.anthropic.AnthropicAiAdapter;
import com.iacodereview.infrastructure.ai.anthropic.RequestBodyBuilder;
import com.iacodereview.infrastructure.ai.anthropic.config.AnthropicRestClientBuilder;
import com.iacodereview.infrastructure.ai.dto.AnalysisPayload;
import com.iacodereview.infrastructure.ai.anthropic.dto.AnthropicResponse;
import com.iacodereview.infrastructure.exception.BadResponseException;
import com.iacodereview.infrastructure.exception.ResponseParsingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnthropicAiAdapterTest {

    private static final String BODY_PATTERN = "Langage: %s\nCode:\n%s";
    private static final Map<String, Object> DUMMY_BODY = Map.of();

    @Mock
    private AnthropicRestClientBuilder restClientBuilder;
    @Mock
    private CodeAnalysisMapper codeAnalysisMapper;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private RestClient restClient;
    @Mock
    private RestClient.RequestBodyUriSpec postSpec;
    @Mock
    private RestClient.RequestBodySpec bodySpec;
    @Mock
    private RestClient.ResponseSpec responseSpec;
    @Mock
    private RequestBodyBuilder requestBodyBuilder;

    @InjectMocks
    private AnthropicAiAdapter adapter;

    private static final String VALID_JSON = "{\"bugs\":[],\"security\":[],\"refactoring\":[],\"quality\":\"ok\",\"score\":8}";

    @BeforeEach
    void setUp() {
        when(restClientBuilder.restClient()).thenReturn(restClient);
        when(restClient.post()).thenReturn(postSpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void analyzeCode_returnsCodeAnalysis_whenClaudeRespondsCorrectly() {
        var payload = new AnalysisPayload(List.of(), List.of(), List.of(), "ok", 8);
        var expected = new CodeAnalysis(List.of(), List.of(), List.of(), "ok", 8);

        when(responseSpec.body(AnthropicResponse.class)).thenReturn(anthropicResponseWithText(VALID_JSON));
        when(objectMapper.readValue(VALID_JSON, AnalysisPayload.class)).thenReturn(payload);
        when(codeAnalysisMapper.toDomain(payload)).thenReturn(expected);
        when(requestBodyBuilder.buildRequestBody(BODY_PATTERN.formatted("java", "code"))).thenReturn(DUMMY_BODY);
        when(postSpec.body(DUMMY_BODY)).thenReturn(bodySpec);

        assertThat(adapter.analyzeCode("code", "java")).isEqualTo(expected);
    }

    @Test
    void analyzeCode_throwsBadResponseException_whenClaudeResponseIsEmpty() {
        when(responseSpec.body(AnthropicResponse.class)).thenReturn(null);
        when(requestBodyBuilder.buildRequestBody(BODY_PATTERN.formatted("java", "code"))).thenReturn(DUMMY_BODY);
        when(postSpec.body(DUMMY_BODY)).thenReturn(bodySpec);

        assertThatThrownBy(() -> adapter.analyzeCode("code", "java"))
                .isInstanceOf(BadResponseException.class)
                .hasMessage("Reponse invalide");
    }

    @Test
    void analyzeCode_throwsIllegalStateException_whenJsonParsingFails() {
        var invalidJson = "not-valid-json";

        when(responseSpec.body(AnthropicResponse.class)).thenReturn(anthropicResponseWithText(invalidJson));
        when(objectMapper.readValue(eq(invalidJson), eq(AnalysisPayload.class)))
                .thenThrow(new RuntimeException("parse error"));
        when(requestBodyBuilder.buildRequestBody(BODY_PATTERN.formatted("java", "code"))).thenReturn(DUMMY_BODY);
        when(postSpec.body(DUMMY_BODY)).thenReturn(bodySpec);

        assertThatThrownBy(() -> adapter.analyzeCode("code", "java"))
                .isInstanceOf(ResponseParsingException.class)
                .hasMessageContaining("Impossible de parser la réponse Claude");
    }

    private static AnthropicResponse anthropicResponseWithText(String text) {
        return new AnthropicResponse(List.of(new AnthropicResponse.ContentBlock("text", text)));
    }
}
