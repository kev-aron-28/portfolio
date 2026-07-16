package com.projects.message_worker.domain;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class Notification {
     private Long id;

    private final Long templateId;

    private final NotificationChannel channel;

    private final String recipient;

    private final Map<String, Object> payload;

    private NotificationStatus status;

    private int retryCount;

    private final String idempotencyKey;

    private LocalDateTime scheduledAt;

    private LocalDateTime sentAt;

    private LocalDateTime deliveredAt;

    private LocalDateTime failedAt;

    private String failureReason;

    private final LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Notification(
            Long templateId,
            NotificationChannel channel,
            String recipient,
            Map<String, Object> payload,
            String idempotencyKey,
            LocalDateTime scheduledAt
    ) {

        this.templateId = templateId;
        this.channel = channel;
        this.recipient = recipient;
        this.payload = payload;

        this.status = NotificationStatus.CREATED;

        this.retryCount = 0;

        this.idempotencyKey = idempotencyKey;

        this.scheduledAt = scheduledAt;

        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Notification create(
            Long templateId,
            NotificationChannel channel,
            String recipient,
            Map<String, Object> payload
    ) {

        return new Notification(
                templateId,
                channel,
                recipient,
                payload,
                UUID.randomUUID().toString(),
                null
        );
    }
    
    public static Notification fromPrimitives(
        Long id,
        Long templateId,
        NotificationStatus status,
        NotificationChannel channel,
        Map<String, Object> payload,
        String recipient,
        int retryCount,
        String idempotencyKey,
        LocalDateTime scheduledAt,
        LocalDateTime sentAt,
        LocalDateTime deliveredAt,
        LocalDateTime failedAt,
        String failureReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    Notification notification = new Notification(templateId, channel, recipient, payload, idempotencyKey, scheduledAt);

    notification.id = id;
    notification.retryCount = retryCount;
    notification.scheduledAt = scheduledAt;
    notification.sentAt = sentAt;
    notification.deliveredAt = deliveredAt;
    notification.failedAt = failedAt;
    notification.failureReason = failureReason;
    notification.updatedAt = updatedAt;
    notification.status = status;

    return notification;
}

    public static Notification schedule(
            Long templateId,
            NotificationChannel channel,
            String recipient,
            Map<String, Object> payload,
            LocalDateTime scheduledAt
    ) {

        return new Notification(
                templateId,
                channel,
                recipient,
                payload,
                UUID.randomUUID().toString(),
                scheduledAt
        );
    }

    public void markQueued() {

        ensureNotFinished();

        status = NotificationStatus.QUEUED;
        touch();
    }

    public void markProcessing() {

        ensureNotFinished();

        status = NotificationStatus.PROCESSING;
        touch();
    }

    public void markSent() {

        ensureNotFinished();

        status = NotificationStatus.SENT;
        sentAt = LocalDateTime.now();

        touch();
    }

    public void markDelivered() {

        if (status != NotificationStatus.SENT) {
            throw new IllegalStateException(
                    "Notification must be SENT before DELIVERED."
            );
        }

        status = NotificationStatus.DELIVERED;

        deliveredAt = LocalDateTime.now();

        touch();
    }

    public void markFailed(String reason) {

        status = NotificationStatus.FAILED;

        failureReason = reason;

        failedAt = LocalDateTime.now();

        touch();
    }

    public void incrementRetry() {

        retryCount++;

        touch();
    }

    public boolean canRetry(int maxRetries) {

        return retryCount < maxRetries;

    }

    public boolean isFinished() {
        return status == NotificationStatus.SENT
                || status == NotificationStatus.FAILED;
    }

    private void ensureNotFinished() {

        if (isFinished()) {
            throw new IllegalStateException(
                    "Notification has already reached a terminal state."
            );
        }

    }

    private void touch() {
        updatedAt = LocalDateTime.now();
    }

    /*
     * Persistence
     */

    public void setId(Long id) {

        if (this.id != null) {
            throw new IllegalStateException("Id already assigned.");
        }

        this.id = id;
    }



    /*
     * Getters
     */

    public Long getId() {
        return id;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public String getRecipient() {
        return recipient;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public LocalDateTime getFailedAt() {
        return failedAt;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Notification [id=" + id + ", templateId=" + templateId + ", channel=" + channel + ", recipient="
                + recipient + ", payload=" + payload + ", status=" + status + ", retryCount=" + retryCount
                + ", idempotencyKey=" + idempotencyKey + ", scheduledAt=" + scheduledAt + ", sentAt=" + sentAt
                + ", deliveredAt=" + deliveredAt + ", failedAt=" + failedAt + ", failureReason=" + failureReason
                + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
    }

    
}
