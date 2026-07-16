package com.projects.api_service.domain.errors;

public class InvalidArgument extends DomainException {

    public InvalidArgument() {
        super("You must provid a valid argument");
    }
    
}
