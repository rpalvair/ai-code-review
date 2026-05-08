package com.iacodereview.infrastructure.ai.anthropic.dto;

import com.iacodereview.infrastructure.exception.BadResponseException;

import java.util.List;

public record AnthropicResponse(List<ContentBlock> content) {

    public record ContentBlock(String type, String text) {}

    public String firstText() {
        return content.stream()
                .filter(b -> "text".equals(b.type()))
                .findFirst()
                .map(ContentBlock::text)
                .orElseThrow(() -> new BadResponseException("Aucun bloc texte dans la réponse Anthropic"));
    }
}
