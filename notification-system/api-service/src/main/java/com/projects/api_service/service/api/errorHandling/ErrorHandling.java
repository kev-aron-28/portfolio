package com.projects.api_service.service.api.errorHandling;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.projects.api_service.service.api.response.ApiResponse;

@RestControllerAdvice
public class ErrorHandling {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleInvalidArguments(MethodArgumentNotValidException ex) {
        Map<String, String> map = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(e -> {
            map.put(e.getField(), e.getDefaultMessage());
        });
        
        return ResponseEntity
            .badRequest()
            .body(ApiResponse.error("Bad request", map));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception ex) {
        return ResponseEntity
            .internalServerError()
            .body(ApiResponse.error("Server error", null));
    }
}
