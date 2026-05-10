package com.projects.api_service.service.api.response;

import java.time.LocalDateTime;

public class ApiResponse<T> {
    private final boolean success;
    private final String message;
    private final T data;
    private final Object error;
    private final LocalDateTime timestamp;

    public ApiResponse(boolean success, String message, T data, Object error) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.error = error;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    public static <T> ApiResponse<T> error(String message, Object error) {
        return new ApiResponse<>(false, message, null, error);   
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Object getError() {
        return error;
    }    
}
