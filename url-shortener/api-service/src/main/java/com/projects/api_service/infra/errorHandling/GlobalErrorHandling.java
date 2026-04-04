package com.projects.api_service.infra.errorHandling;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.projects.api_service.infra.ApiResponse;

@RestControllerAdvice
public class GlobalErrorHandling {
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGeneric(Exception ex) {
        return ResponseEntity
            .internalServerError()
            .body(ApiResponse.error("Unexpected error", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleBadRequest(Exception ex) {
        return ResponseEntity
            .badRequest()
            .body(ApiResponse.error("Bad request", ex.getMessage()));
    }
}
