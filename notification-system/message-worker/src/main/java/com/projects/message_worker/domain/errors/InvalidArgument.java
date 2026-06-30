package com.projects.message_worker.domain.errors;

public class InvalidArgument extends DomainException {

    public InvalidArgument() {
        super("You must provid a valid argument");
    }
    
}
