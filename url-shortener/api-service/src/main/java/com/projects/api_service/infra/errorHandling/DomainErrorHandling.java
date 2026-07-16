package com.projects.api_service.infra.errorHandling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.projects.api_service.domain.exceptions.HashGenerationException;
import com.projects.api_service.domain.exceptions.ShortUrlNotFound;
import com.projects.api_service.infra.ApiResponse;

@RestControllerAdvice
public class DomainErrorHandling {
    @ExceptionHandler(HashGenerationException.class)
    public ResponseEntity<ApiResponse<String>> handleError(HashGenerationException ex) {
        return ResponseEntity.badRequest().body(
            ApiResponse.error("Unexpected error", ex.getMessage())
        );
    } 

    @ExceptionHandler(ShortUrlNotFound.class)
    public ResponseEntity<ApiResponse<String>> handleUrlNotFound(ShortUrlNotFound ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ApiResponse.error("Not found", ex.getMessage())
        );
    }
}
