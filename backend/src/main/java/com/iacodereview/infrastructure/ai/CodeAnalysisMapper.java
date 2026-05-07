package com.iacodereview.infrastructure.ai;

import org.springframework.stereotype.Component;

import com.iacodereview.domain.model.CodeAnalysis;
import com.iacodereview.infrastructure.ai.dto.AnalysisPayload;

@Component
class CodeAnalysisMapper {

    CodeAnalysis toDomain(AnalysisPayload dto) {
        return new CodeAnalysis(dto.bugs(), dto.security(), dto.refactoring(), dto.quality(), dto.score());
    }
}
