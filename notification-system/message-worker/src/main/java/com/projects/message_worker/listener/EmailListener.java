package com.projects.message_worker.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EmailListener {
    
    @RabbitListener(queues = rabbi)
    public void consume() {

    }
}
