package com.iacodereview.domain.model;

import java.util.List;

public record CodeAnalysis(
        List<String> bugs,
        List<String> security,
        List<String> refactoring,
        String quality,
        int score
) {}
