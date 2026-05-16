package com.projects.api_service.application;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.projects.api_service.domain.message.NotificationMessage;
import com.projects.api_service.service.queue.RabbitMQConfig;

@Service
public class SendNotification {
    private final RabbitTemplate rabbitTemplate;

    public SendNotification(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(NotificationMessage message) {

        String routingKey = switch(message.getChannel()) {
            case EMAIL -> "notification.email";
            case SMS -> "notification.sms";
        };

        rabbitTemplate.convertAndSend(
            RabbitMQConfig.NOTIFICATION_EXCHANGE,
            routingKey,
            message
        );
    }
}
