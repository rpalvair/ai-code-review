package com.iacodereview.infrastructure.ai.anthropic;

import com.iacodereview.infrastructure.ai.anthropic.config.AnthropicProperties;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestBodyBuilderTest {

    private RequestBodyBuilder requestBodyBuilder;
    @Mock
    private AnthropicProperties anthropicProperties;
    @Mock
    private Resource systemPromptResource;

    private static final String SYSTEM_PROMPT = "Tu es un ingénieur senior qui effectue une revue de code.";

    @BeforeEach
    void setUp() throws IOException {
        when(systemPromptResource.getContentAsString(StandardCharsets.UTF_8)).thenReturn(SYSTEM_PROMPT);
        requestBodyBuilder = new RequestBodyBuilder(anthropicProperties, systemPromptResource);
    }

    @Test
    void analyzeCode_returnMap_whenEverythingIsOK() {
        when(anthropicProperties.model()).thenReturn("claude-test");
        when(anthropicProperties.maxTokens()).thenReturn(1024);

        final Map<String, Object> body = requestBodyBuilder.buildRequestBody("dummyMessage");

        assertThat(body.get("model")).isEqualTo("claude-test");
        assertThat(body.get("max_tokens")).isEqualTo(1024);
        assertThat(body.get("system")).isEqualTo(SYSTEM_PROMPT);
        assertThat(body.get("messages"))
                .asInstanceOf(InstanceOfAssertFactories.list(Map.class))
                .hasSize(1)
                .first()
                .asInstanceOf(InstanceOfAssertFactories.map(String.class, Object.class))
                .containsEntry("role", "user")
                .containsEntry("content", "dummyMessage");
    }
}
