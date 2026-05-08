package com.iacodereview.infrastructure.exception;

public class BadResponseException extends RuntimeException {

    public BadResponseException(String message) {
        super(message);
    }
}
