package com.iacodereview.infrastructure.ai.dto;

import java.util.List;

public record AnalysisPayload(
        List<String> bugs,
        List<String> security,
        List<String> refactoring,
        String quality,
        int score
) {}
