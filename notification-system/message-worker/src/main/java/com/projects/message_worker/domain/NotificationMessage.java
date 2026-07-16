package com.projects.message_worker.domain;

public class NotificationMessage {
    private final Long notificationId;

    public NotificationMessage(Long notificationId) {
        this.notificationId = notificationId;
    }

    public Long getNotificationId() {
        return notificationId;
    }
}
