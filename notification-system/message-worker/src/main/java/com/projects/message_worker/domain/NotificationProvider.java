package com.projects.message_worker.domain;

public interface NotificationProvider {
    void send(Notification notification, String content);
}
