package com.iacodereview.infrastructure.ai;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.iacodereview.infrastructure.ai.dto.AnalysisPayload;

class CodeAnalysisMapperTest {

    private final CodeAnalysisMapper mapper = new CodeAnalysisMapper();

    @Test
    void toDomain_mapsAllFields() {
        var payload = new AnalysisPayload(
                List.of("NullPointerException possible"),
                List.of("Injection SQL"),
                List.of("Extraire méthode"),
                "Code de qualité moyenne",
                6
        );

        var result = mapper.toDomain(payload);

        assertThat(result.bugs()).containsExactly("NullPointerException possible");
        assertThat(result.security()).containsExactly("Injection SQL");
        assertThat(result.refactoring()).containsExactly("Extraire méthode");
        assertThat(result.quality()).isEqualTo("Code de qualité moyenne");
        assertThat(result.score()).isEqualTo(6);
    }

    @Test
    void toDomain_mapsEmptyLists() {
        var payload = new AnalysisPayload(List.of(), List.of(), List.of(), "Code propre", 9);

        var result = mapper.toDomain(payload);

        assertThat(result.bugs()).isEmpty();
        assertThat(result.security()).isEmpty();
        assertThat(result.refactoring()).isEmpty();
        assertThat(result.quality()).isEqualTo("Code propre");
        assertThat(result.score()).isEqualTo(9);
    }
}
