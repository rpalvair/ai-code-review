package com.iacodereview.infrastructure.web;

import com.iacodereview.domain.model.CodeAnalysis;
import com.iacodereview.domain.model.Review;
import com.iacodereview.domain.port.in.ReviewCodeUseCase;
import com.iacodereview.infrastructure.exception.BadResponseException;
import com.iacodereview.infrastructure.exception.ResponseParsingException;
import com.iacodereview.infrastructure.web.dto.AnalysisResponse;
import com.iacodereview.infrastructure.web.dto.ReviewResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewCodeUseCase reviewCodeUseCase;

    @MockitoBean
    private ReviewMapper reviewMapper;

    @Test
    void review_returns200WithCompleteResponse_whenRequestIsValid() throws Exception {
        var id = UUID.randomUUID();
        var createdAt = Instant.now();
        final List<String> bugs = List.of("bug1");
        final List<String> security = List.of("sec1");
        final List<String> refactoring = List.of("ref1");
        final String quality = "Bonne qualité";
        final int score = 8;
        final String language = "java";
        var analysis = new CodeAnalysis(bugs, security, refactoring, quality, score);
        var review = new Review(id, language, createdAt, analysis);
        var analysisResponse = new AnalysisResponse(bugs, security, refactoring, quality, score);
        var response = new ReviewResponse(id, language, createdAt, analysisResponse);

        when(reviewCodeUseCase.execute("System.out.println()", language)).thenReturn(review);
        when(reviewMapper.toResponse(review)).thenReturn(response);

        mockMvc.perform(post("/api/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code": "System.out.println()", "language": "java"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.language").value(language))
                .andExpect(jsonPath("$.analysis.score").value(score))
                .andExpect(jsonPath("$.analysis.quality").value(quality))
                .andExpect(jsonPath("$.analysis.bugs[0]").value("bug1"))
                .andExpect(jsonPath("$.analysis.security[0]").value("sec1"))
                .andExpect(jsonPath("$.analysis.refactoring[0]").value("ref1"));
    }

    @Test
    void review_passesExactCodeAndLanguageToUseCase() throws Exception {
        var id = UUID.randomUUID();
        var createdAt = Instant.now();
        var analysis = new CodeAnalysis(List.of(), List.of(), List.of(), "ok", 7);
        final String language = "python";
        var review = new Review(id, language, createdAt, analysis);
        var response = new ReviewResponse(id, language, createdAt, new AnalysisResponse(List.of(), List.of(), List.of(), "ok", 7));
        final String code = "x = 1";

        when(reviewCodeUseCase.execute(code, language)).thenReturn(review);
        when(reviewMapper.toResponse(review)).thenReturn(response);

        mockMvc.perform(post("/api/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code": "x = 1", "language": "python"}
                                """))
                .andExpect(status().isOk());

        verify(reviewCodeUseCase).execute(code, language);
    }

    @Test
    void review_returns400_whenCodeIsBlank() throws Exception {
        mockMvc.perform(post("/api/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code": "", "language": "java"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void review_returns400_whenLanguageIsBlank() throws Exception {
        mockMvc.perform(post("/api/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code": "System.out.println()", "language": ""}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void review_returns400_whenBodyIsEmpty() throws Exception {
        mockMvc.perform(post("/api/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void review_returns400_whenBadResponseExceptionIsThrown() throws Exception {
        final String language = "python";
        final String code = "x = 1";
        doThrow(new BadResponseException("")).when(reviewCodeUseCase).execute(code, language);

        mockMvc.perform(post("/api/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code": "x = 1", "language": "python"}
                                """))
                .andExpect(status().isBadRequest());

        verify(reviewCodeUseCase).execute(code, language);
    }

    @Test
    void review_returns500_whenResponseParsingExceptionIsThrown() throws Exception {
        final String language = "python";
        final String code = "x = 1";
        doThrow(new ResponseParsingException("Oups", new RuntimeException("Voici la cause"))).when(reviewCodeUseCase).execute(code, language);

        mockMvc.perform(post("/api/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code": "x = 1", "language": "python"}
                                """))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Oups"));

        verify(reviewCodeUseCase).execute(code, language);
    }
}
