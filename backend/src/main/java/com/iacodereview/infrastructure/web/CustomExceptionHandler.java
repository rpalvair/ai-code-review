package com.iacodereview.infrastructure.web;

import com.iacodereview.infrastructure.exception.BadResponseException;
import com.iacodereview.infrastructure.exception.ResponseParsingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler(BadResponseException.class)
    public ResponseEntity<ErrorMessage> handleBadResponseException(BadResponseException exception) {
        LOGGER.error("Une erreur est survenue", exception);
        return ResponseEntity.badRequest().body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(ResponseParsingException.class)
    public ResponseEntity<ErrorMessage> handleResponseParsingException(ResponseParsingException exception) {
        LOGGER.error("Une erreur est survenue", exception);
        return ResponseEntity.internalServerError().body(new ErrorMessage(exception.getMessage()));
    }

}
