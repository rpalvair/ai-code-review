package com.iacodereview.infrastructure.web;

import org.springframework.stereotype.Component;

import com.iacodereview.domain.model.Review;
import com.iacodereview.infrastructure.web.dto.AnalysisResponse;
import com.iacodereview.infrastructure.web.dto.ReviewResponse;

@Component
class ReviewMapper {

    ReviewResponse toResponse(Review review) {
        AnalysisResponse analysis = new AnalysisResponse(
                review.analysis().bugs(),
                review.analysis().security(),
                review.analysis().refactoring(),
                review.analysis().quality(),
                review.analysis().score()
        );
        return new ReviewResponse(review.id(), review.language(), review.createdAt(), analysis);
    }
}
