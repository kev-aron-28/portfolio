package com.projects.api_service.domain.errors;

public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
    
}
