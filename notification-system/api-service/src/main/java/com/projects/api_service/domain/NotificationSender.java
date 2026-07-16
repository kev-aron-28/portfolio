package com.projects.api_service.domain;

public interface NotificationSender {
    void publish(Long notificationId, NotificationChannel channel);  
}

