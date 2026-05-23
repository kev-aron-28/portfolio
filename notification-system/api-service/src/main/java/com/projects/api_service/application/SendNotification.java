package com.projects.api_service.application;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.projects.api_service.domain.Template;
import com.projects.api_service.domain.errors.ChannelMismatch;
import com.projects.api_service.domain.services.TemplatePayloadValidator;
import com.projects.api_service.service.api.dto.SendNotificationDto;

@Service
public class SendNotification {
    private final RabbitTemplate rabbitTemplate;
    private final GetTemplate getTemplate;
    private final TemplatePayloadValidator validator;

    public SendNotification(RabbitTemplate rabbitTemplate, GetTemplate getTemplate, TemplatePayloadValidator validator) {
        this.rabbitTemplate = rabbitTemplate;
        this.getTemplate = getTemplate;
        this.validator = validator;
    }

    public void publish(SendNotificationDto notification) {
        Template template = this.getTemplate.run(notification.templateId());
        
        this.validateTemplate(template, notification);

        this.validator.validate(template.getContent(), notification.payload());

        String routingKey = switch(notification.channel()) {
            case EMAIL -> "notification.email";
            case SMS -> "notification.sms";
        };

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
