package com.projects.message_worker.domain.errors;

public class NotificationNotFound extends DomainException {

    public NotificationNotFound() {
        super("Notification not found");
    }
    
}
