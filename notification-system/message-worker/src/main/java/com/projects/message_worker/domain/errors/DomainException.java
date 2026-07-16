package com.projects.message_worker.domain.errors;

public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
    
}
