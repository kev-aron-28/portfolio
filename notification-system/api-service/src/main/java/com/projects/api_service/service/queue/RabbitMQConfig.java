package com.projects.api_service.service.queue;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";

    public static final String EMAIL_QUEUE = "email.queue";

    public static final String SMS_QUEUE = "sms.queue";

    public static final String EMAIL_ROUTING_KEY = "notification.email";

    public static final String SMS_ROUTING_KEY = "notification.sms";

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE);
    }

    @Bean
    public Queue emailQueue() {
        return new Queue(EMAIL_QUEUE);
    }

    @Bean
    public Queue smsQueue() {
        return new Queue(SMS_QUEUE);
    }

    @Bean
    public Binding emailBinding() {
        return BindingBuilder
            .bind(emailQueue())
            .to(notificationExchange())
            .with(EMAIL_ROUTING_KEY);
    }

    @Bean
    public Binding smsBinding() {
        return BindingBuilder
                .bind(smsQueue())
                .to(notificationExchange())
                .with(SMS_ROUTING_KEY);
    }

}
