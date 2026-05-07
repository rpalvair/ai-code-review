package com.iacodereview.domain.port.out;

import com.iacodereview.domain.model.CodeAnalysis;

public interface AiReviewPort {

    CodeAnalysis analyzeCode(String code, String language);
}
