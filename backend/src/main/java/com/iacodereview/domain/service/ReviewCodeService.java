package com.iacodereview.domain.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.iacodereview.domain.model.CodeAnalysis;
import com.iacodereview.domain.model.Review;
import com.iacodereview.domain.port.in.ReviewCodeUseCase;
import com.iacodereview.domain.port.out.AiReviewPort;

@Service
public class ReviewCodeService implements ReviewCodeUseCase {

    private final AiReviewPort aiReviewPort;

    public ReviewCodeService(AiReviewPort aiReviewPort) {
        this.aiReviewPort = aiReviewPort;
    }

    @Override
    public Review execute(String code, String language) {
        CodeAnalysis codeAnalysis = aiReviewPort.analyzeCode(code, language);
        return new Review(UUID.randomUUID(), language, Instant.now(), codeAnalysis);
    }
}
