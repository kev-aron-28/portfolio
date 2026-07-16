package com.projects.message_worker.domain;

import java.util.Optional;


public interface NotificationRepository {
    public void update(Notification notification);
    public Optional<Notification> findById(Long id);
}
