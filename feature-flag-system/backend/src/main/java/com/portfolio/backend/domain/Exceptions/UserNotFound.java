package com.portfolio.backend.domain.Exceptions;

public class UserNotFound extends DomainException {

    public UserNotFound() {
        super("USER_NOT_FOUND", "The specified user was not found");
    }
    
}
