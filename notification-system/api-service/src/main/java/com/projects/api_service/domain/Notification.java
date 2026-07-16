package com.projects.api_service.domain;

import java.util.Map;

public class Notification {
    private Long id;
    private Long templateId;
    private NotificationStatus status;
    private NotificationChannel channel;
    private Map<String, Object> payload;
    private String recipient;

    public Notification(Long templateId, NotificationStatus status, NotificationChannel channel, Map<String, Object> payload, String recipient) {
        this.templateId = templateId;
        this.status = status;
        this.channel = channel;
        this.payload = payload;
        this.recipient = recipient;
   }

   public static Notification create(Long templateId, NotificationChannel channel, Map<String, Object> payload, String recipient) {
        return new Notification(
            templateId, 
            NotificationStatus.CREATED, 
            channel, 
            payload,
            recipient
        );
   }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }


    public NotificationStatus getStatus() {
        return status;
    }


    public void setStatus(NotificationStatus status) {
        this.status = status;
    }


    public NotificationChannel getChannel() {
        return channel;
    }


    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }


    public Map<String, Object> getPayload() {
        return payload;
    }


    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getRecipient() {
        return recipient;
    }
}
