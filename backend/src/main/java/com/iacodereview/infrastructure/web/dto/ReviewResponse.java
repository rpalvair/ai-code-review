package com.iacodereview.infrastructure.web.dto;

import java.time.Instant;
import java.util.UUID;

public record ReviewResponse(
        UUID id,
        String language,
        Instant createdAt,
        AnalysisResponse analysis
) {}
