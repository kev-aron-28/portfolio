package com.projects.api_service.application;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.projects.api_service.domain.Notification;
import com.projects.api_service.domain.NotificationRepository;
import com.projects.api_service.domain.Template;
import com.projects.api_service.domain.errors.ChannelMismatch;
import com.projects.api_service.domain.services.TemplatePayloadValidator;
import com.projects.api_service.service.api.dto.SendNotificationDto;

@Service
public class SendNotification {
    private final RabbitTemplate rabbitTemplate;
    private final GetTemplate getTemplate;
    private final TemplatePayloadValidator validator;
    private final NotificationRepository repository;

    public SendNotification(RabbitTemplate rabbitTemplate, GetTemplate getTemplate, TemplatePayloadValidator validator, NotificationRepository repository) {
        this.rabbitTemplate = rabbitTemplate;
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

        String routingKey = switch(dto.channel()) {
            case EMAIL -> "notification.email";
            case SMS -> "notification.sms";
        };

        Notification notification = Notification.create(
                dto.templateId(),
                dto.channel(),
                dto.payload(),
                dto.recipient()
            );

        this.repository.save(notification);
        // [TODO]: Create a infra service for notifications

        // [TODO]: Send to exchange

        // this.rabbitTemplate.send(routingKey, message);
        // this.rabbitTemplate.convertAndSend(
        //     RabbitMQConfig.NOTIFICATION_EXCHANGE,
        //     routingKey,
        //     message
        // );
    }

    private void validateTemplate(Template template, SendNotificationDto dto) {
        if(template.getChannel() != dto.channel()) {
            throw new ChannelMismatch();
        }
    }
}
