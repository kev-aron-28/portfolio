package com.projects.api_service.service.queue;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.projects.api_service.domain.Notification;
import com.projects.api_service.domain.NotificationChannel;
import com.projects.api_service.domain.NotificationMessage;
import com.projects.api_service.domain.NotificationSender;

@Service
public class RabbitNotificationSender implements NotificationSender {
    private final RabbitTemplate rabbitTemplate;

    public RabbitNotificationSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(Long notificationId, NotificationChannel channel) {
        String routingKey = switch(channel) {
            case EMAIL -> RabbitMQConfig.EMAIL_ROUTING_KEY;
            case SMS -> RabbitMQConfig.SMS_ROUTING_KEY;
        };

        NotificationMessage message = new NotificationMessage(notificationId);

        rabbitTemplate.convertAndSend(
            
        );

        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
