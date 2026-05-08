package com.iacodereview.infrastructure.web;

import com.iacodereview.infrastructure.exception.BadResponseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(BadResponseException.class)
    public ResponseEntity<ErrorMessage> handleBadResponseException(BadResponseException exc) {
        return ResponseEntity.badRequest().body(new ErrorMessage(exc.getMessage()));
    }

}
