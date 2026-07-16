package com.projects.message_worker.domain.errors;

public class TemporaryProviderException extends DomainException {

    public TemporaryProviderException() {
        super("Provider failed");
    }

}
