package com.portfolio.backend.domain.Exceptions;

public class InvalidRollout extends DomainException {

    public InvalidRollout() {
        super("INVALID_ROLLOUT", "The rollout value must be between 0 and 100");
    }
    
}
