package com.iacodereview.domain.model;

import java.time.Instant;
import java.util.UUID;

public record Review(
        UUID id,
        String language,
        Instant createdAt,
        CodeAnalysis analysis
) {}
