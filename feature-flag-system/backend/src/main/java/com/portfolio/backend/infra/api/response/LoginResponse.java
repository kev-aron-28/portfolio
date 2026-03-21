package com.portfolio.backend.infra.api.response;

public class LoginResponse {
    private final String message = "User succesfully authenticated";
    private final String token;

    public LoginResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }
    
    
    
}
