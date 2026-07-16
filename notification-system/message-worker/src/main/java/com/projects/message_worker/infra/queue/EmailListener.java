package com.projects.message_worker.infra.queue;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.projects.message_worker.application.ProcessNotification;
import com.projects.message_worker.domain.NotificationMessage;

@Component
public class EmailListener {

    private final ProcessNotification process;

    public EmailListener(ProcessNotification process) {
        this.process = process;
    }
    
    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void consume(NotificationMessage message) {
        this.process.run(message);
    }
}
