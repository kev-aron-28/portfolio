package com.projects.api_service.application;

import org.springframework.stereotype.Service;

import com.projects.api_service.domain.Notification;
import com.projects.api_service.domain.NotificationRepository;
import com.projects.api_service.domain.NotificationSender;
import com.projects.api_service.domain.Template;
import com.projects.api_service.domain.errors.ChannelMismatch;
import com.projects.api_service.domain.services.TemplatePayloadValidator;
import com.projects.api_service.service.api.dto.SendNotificationDto;

@Service
public class SendNotification {
    private final NotificationSender notificationSender;
    private final GetTemplate getTemplate;
    private final TemplatePayloadValidator validator;
    private final NotificationRepository repository;

    public SendNotification(NotificationSender notificationSender, GetTemplate getTemplate, TemplatePayloadValidator validator, NotificationRepository repository) {
        this.notificationSender = notificationSender;
        this.getTemplate = getTemplate;
        this.validator = new TemplatePayloadValidator();
        this.repository = repository;
    }

    public void publish(SendNotificationDto dto) {
        Template template = this.getTemplate.run(dto.templateId());
        
        //  Channel must be the same as the template
        this.validateTemplate(template, dto);

        // Must check paylaod fields with template fields
        this.validator.validate(template.getContent(), dto.payload());

        // Instantiate the notification to send
        Notification notification = Notification.create(
                dto.templateId(),
                dto.channel(),
                dto.payload(),
                dto.recipient()
            );

        Long id = this.repository.save(notification);
        
        this.notificationSender.publish(id, notification.getChannel());
    }

    private void validateTemplate(Template template, SendNotificationDto dto) {
        if(template.getChannel() != dto.channel()) {
            throw new ChannelMismatch();
        }
    }
}
