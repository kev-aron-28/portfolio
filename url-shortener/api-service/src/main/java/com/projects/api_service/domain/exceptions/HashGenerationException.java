package com.projects.api_service.domain.exceptions;

public class HashGenerationException extends RuntimeException {
    public HashGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
