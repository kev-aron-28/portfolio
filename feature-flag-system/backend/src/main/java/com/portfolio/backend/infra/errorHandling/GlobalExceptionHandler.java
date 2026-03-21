package com.portfolio.backend.infra.errorHandling;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> handleNotFound(NoHandlerFoundException ex) {
        return ResponseEntity.status(404).body(Map.of(
            "error", "NOT_FOUND",
            "message", "Endpoint no existe"
        ));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(405).body(Map.of(
            "error", "METHOD_NOT_ALLOWED",
            "message", "Método HTTP no permitido"
        ));
    }
}
