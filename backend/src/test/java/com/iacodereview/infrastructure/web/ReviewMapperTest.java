package com.iacodereview.infrastructure.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.iacodereview.domain.model.CodeAnalysis;
import com.iacodereview.domain.model.Review;

class ReviewMapperTest {

    private final ReviewMapper mapper = new ReviewMapper();

    @Test
    void toResponse_mapsAllFields() {
        var id = UUID.randomUUID();
        var createdAt = Instant.now();
        var analysis = new CodeAnalysis(
                List.of("bug1"),
                List.of("sec1"),
                List.of("ref1"),
                "Bonne qualité globale",
                8
        );
        var review = new Review(id, "java", createdAt, analysis);

        var result = mapper.toResponse(review);

        assertThat(result.id()).isEqualTo(id);
        assertThat(result.language()).isEqualTo("java");
        assertThat(result.createdAt()).isEqualTo(createdAt);
        assertThat(result.analysis().bugs()).containsExactly("bug1");
        assertThat(result.analysis().security()).containsExactly("sec1");
        assertThat(result.analysis().refactoring()).containsExactly("ref1");
        assertThat(result.analysis().quality()).isEqualTo("Bonne qualité globale");
        assertThat(result.analysis().score()).isEqualTo(8);
    }
}
