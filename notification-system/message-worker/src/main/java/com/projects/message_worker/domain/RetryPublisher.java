package com.projects.message_worker.domain;

public interface RetryPublisher {
    void publish(Notification notification);
}
