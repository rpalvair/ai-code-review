package com.iacodereview.infrastructure.web.dto;

import java.util.List;

public record AnalysisResponse(
        List<String> bugs,
        List<String> security,
        List<String> refactoring,
        String quality,
        int score
) {}
