package com.portfolio.backend.domain.Exceptions;

public class InvalidId extends DomainException {

    public InvalidId() {
        super("INVALID_UUID", "You must provide a valid UUID");
    }
}
