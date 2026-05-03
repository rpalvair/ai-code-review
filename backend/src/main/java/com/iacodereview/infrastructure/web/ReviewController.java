package com.iacodereview.infrastructure.web;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iacodereview.domain.model.Review;
import com.iacodereview.domain.port.in.ReviewCodeUseCase;
import com.iacodereview.infrastructure.web.dto.ReviewRequest;
import com.iacodereview.infrastructure.web.dto.ReviewResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@Validated
public class ReviewController {

    private final ReviewCodeUseCase reviewCodeUseCase;
    private final ReviewMapper reviewMapper;

    public ReviewController(ReviewCodeUseCase reviewCodeUseCase, ReviewMapper reviewMapper) {
        this.reviewCodeUseCase = reviewCodeUseCase;
        this.reviewMapper = reviewMapper;
    }

    @PostMapping("/review")
    public ResponseEntity<ReviewResponse> review(@Valid @RequestBody ReviewRequest request) {
        Review review = reviewCodeUseCase.execute(request.code(), request.language());
        return ResponseEntity.ok(reviewMapper.toResponse(review));
    }
}
