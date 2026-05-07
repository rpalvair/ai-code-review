package com.iacodereview.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public record ReviewRequest(
        @NotBlank(message = "Le code ne peut pas être vide") String code,
        @NotBlank(message = "Le langage ne peut pas être vide") String language
) {}
