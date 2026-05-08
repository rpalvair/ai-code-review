package com.iacodereview.infrastructure.ai.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import com.iacodereview.infrastructure.ai.anthropic.dto.AnthropicResponse;
import org.junit.jupiter.api.Test;

class AnthropicResponseTest {

    @Test
    void firstText_returnsText_whenSingleTextBlock() {
        var response = new AnthropicResponse(List.of(
                new AnthropicResponse.ContentBlock("text", "hello")
        ));

        var result = response.firstText();

        assertThat(result).isEqualTo("hello");
    }

    @Test
    void firstText_returnsFirstTextBlock_whenMultipleBlocksPresent() {
        var response = new AnthropicResponse(List.of(
                new AnthropicResponse.ContentBlock("tool_use", null),
                new AnthropicResponse.ContentBlock("text", "first"),
                new AnthropicResponse.ContentBlock("text", "second")
        ));

        var result = response.firstText();

        assertThat(result).isEqualTo("first");
    }

    @Test
    void firstText_throwsIllegalStateException_whenNoTextBlock() {
        var response = new AnthropicResponse(List.of(
                new AnthropicResponse.ContentBlock("tool_use", null)
        ));

        assertThatThrownBy(response::firstText)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Aucun bloc texte dans la réponse Anthropic");
    }
}
