package com.projects.job_tracker.presentation.api;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.projects.job_tracker.domain.exception.ResourceNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(com.projects.job_tracker.domain.observatory.DuplicateIdentityException.class)
    public ResponseEntity<Map<String,Object>> handleDuplicateIdentity(com.projects.job_tracker.domain.observatory.DuplicateIdentityException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error",ex.getMessage(),"matchingPostingIds",ex.getMatches()));
    }


	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
	}
}
