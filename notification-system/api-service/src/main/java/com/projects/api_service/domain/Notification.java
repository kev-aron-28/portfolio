package com.projects.api_service.domain;

import java.util.Map;

public class Notification {
    private final Long id;
    private Long templateId;
    private NotificationStatus status;
    private Map<String, Object> payload;


}
