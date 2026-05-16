package com.projects.api_service.domain.message;

import com.projects.api_service.domain.NotificationChannel;

public class NotificationMessage {
    private Long notificationId;
    private NotificationChannel channel;

    public NotificationMessage() {
    }

    public NotificationMessage(Long notificationId, NotificationChannel channel) {
        this.notificationId = notificationId;
        this.channel = channel;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public NotificationChannel getChannel() {
        return channel;
    }
}
