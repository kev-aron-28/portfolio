package com.projects.message_worker.domain;

public enum NotificationStatus {
    CREATED,
    QUEUED,
    PROCESSING,
    SENT,
    FAILED,
    DELIVERED
}
