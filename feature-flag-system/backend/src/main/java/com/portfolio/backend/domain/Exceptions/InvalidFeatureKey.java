package com.portfolio.backend.domain.Exceptions;

public class InvalidFeatureKey extends DomainException {

    public InvalidFeatureKey() {
        super("INVALID_FEATURE_KEY", "The key for a Feature Flag must not be null or empty");
    }
    
}
