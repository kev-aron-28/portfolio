package com.projects.message_worker.application;

import org.springframework.stereotype.Service;

import com.projects.message_worker.domain.Notification;
import com.projects.message_worker.domain.NotificationMessage;
import com.projects.message_worker.domain.NotificationProvider;
import com.projects.message_worker.domain.NotificationRepository;
import com.projects.message_worker.domain.RetryPublisher;
import com.projects.message_worker.domain.Template;
import com.projects.message_worker.domain.TemplateRepository;
import com.projects.message_worker.domain.errors.NotificationNotFound;
import com.projects.message_worker.domain.errors.TemplateNotFound;
import com.projects.message_worker.domain.errors.TemporaryProviderException;
import com.projects.message_worker.domain.services.TemplateRenderer;

@Service
public class ProcessNotification {

    private final NotificationRepository repository;
    private final TemplateRepository templateRepository;
    private final NotificationProvider provider;
    private final TemplateRenderer renderer;
    private final RetryPublisher retryPublisher;

    public ProcessNotification(NotificationRepository repository, TemplateRepository templateRepository, NotificationProvider provider, TemplateRenderer renderer, RetryPublisher retryPublisher) {
        this.repository = repository;
        this.templateRepository = templateRepository;
        this.provider = provider;
        this.renderer = new TemplateRenderer();
        this.retryPublisher = retryPublisher;
    }

    public void run(NotificationMessage message) {
        Notification notification = repository.findById(message.getNotificationId()).orElseThrow(() -> new NotificationNotFound());

        if(notification.isFinished()) return;

        notification.markProcessing();

        repository.update(notification);

        try {
            Template template = templateRepository
                .findById(notification.getTemplateId())
                .orElseThrow(() -> new TemplateNotFound(notification.getTemplateId()));

            String content = renderer.render(template.getContent(), notification.getPayload());

            System.out.println("CONTENT: " + content);

            provider.send(notification, content);

            notification.markSent();

            repository.update(notification);
        } catch (TemporaryProviderException e) {
            notification.incrementRetry();

            if(notification.canRetry(3)) {
                notification.markQueued();

                repository.update(notification);

                this.retryPublisher.publish(notification);
                
            } else {
                notification.markFailed(e.getMessage());

                repository.update(notification);
            }
        } catch(Exception e) {
            notification.markFailed(e.getMessage());

            repository.update(notification);

            throw e;
        }
    }
}
