package com.projects.message_worker.domain.errors;

public class ChannelMismatch extends DomainException {

    public ChannelMismatch() {
        super("The channel must match the template channel");
    }
    
}
