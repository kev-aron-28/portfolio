package com.projects.message_worker.infra.queue;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.projects.message_worker.domain.Notification;
import com.projects.message_worker.domain.NotificationMessage;
import com.projects.message_worker.domain.RetryPublisher;

@Component
public class RabbitRetryPublisher implements RetryPublisher {
    private final RabbitTemplate rabbitTemplate;

    public RabbitRetryPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(Notification notification) {
        NotificationMessage message = new NotificationMessage(notification.getId());

        rabbitTemplate.convertAndSend(
            RabbitMQConfig.NOTIFICATION_EXCHANGE,
            routingKey(notification),
            message
        );
    }

     private String routingKey(Notification notification) {
        return switch (notification.getChannel()) {
            case EMAIL -> RabbitMQConfig.EMAIL_ROUTING_KEY;
            case SMS -> RabbitMQConfig.SMS_ROUTING_KEY;
        };
    }
}
