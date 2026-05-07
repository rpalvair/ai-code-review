package com.iacodereview.domain.port.in;

import com.iacodereview.domain.model.Review;

public interface ReviewCodeUseCase {

    Review execute(String code, String language);
}
