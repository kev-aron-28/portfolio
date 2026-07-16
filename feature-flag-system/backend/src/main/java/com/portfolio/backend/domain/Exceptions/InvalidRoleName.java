package com.portfolio.backend.domain.Exceptions;

public class InvalidRoleName extends DomainException {

    public InvalidRoleName() {
        super("INVALID_ROLE_NAME", "You must provide a valid Role name");
    }
    
}
