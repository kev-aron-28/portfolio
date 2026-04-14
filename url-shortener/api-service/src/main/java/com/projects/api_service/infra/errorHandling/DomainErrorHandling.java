package com.projects.api_service.infra.errorHandling;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.projects.api_service.domain.exceptions.HashGenerationException;

@RestControllerAdvice
public class DomainErrorHandling {
    @ExceptionHandler(HashGenerationException.class)
    public String handleError(HashGenerationException ex) {
        return "NEW";
    }  
}
