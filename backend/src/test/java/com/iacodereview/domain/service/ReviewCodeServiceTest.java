package com.iacodereview.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.iacodereview.domain.model.CodeAnalysis;
import com.iacodereview.domain.port.out.AiReviewPort;

@ExtendWith(MockitoExtension.class)
class ReviewCodeServiceTest {

    @Mock
    private AiReviewPort aiReviewPort;

    @InjectMocks
    private ReviewCodeService service;

    @Test
    void execute_returnsReview_withAnalysisFromPort() {
        var analysis = new CodeAnalysis(List.of("bug"), List.of(), List.of(), "ok", 7);
        final String code = "code";
        final String language = "java";
        when(aiReviewPort.analyzeCode(code, language)).thenReturn(analysis);

        var result = service.execute(code, language);

        assertThat(result.analysis()).isEqualTo(analysis);
        assertThat(result.language()).isEqualTo(language);
        verify(aiReviewPort).analyzeCode(code, language);
    }

    @Test
    void execute_generatesDifferentIds_onConsecutiveCalls() {
        var analysis = new CodeAnalysis(List.of(), List.of(), List.of(), "ok", 7);
        when(aiReviewPort.analyzeCode(any(), any())).thenReturn(analysis);

        var first = service.execute("code", "java");
        var second = service.execute("code", "java");

        assertThat(first.id()).isNotEqualTo(second.id());
    }
}
