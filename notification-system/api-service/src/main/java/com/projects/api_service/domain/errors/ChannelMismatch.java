package com.projects.api_service.domain.errors;

public class ChannelMismatch extends DomainException {

    public ChannelMismatch() {
        super("The channel must match the template channel");
    }
    
}
