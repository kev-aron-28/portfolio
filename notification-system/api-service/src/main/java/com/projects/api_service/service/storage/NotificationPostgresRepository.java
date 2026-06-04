package com.projects.api_service.service.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.projects.api_service.domain.Notification;
import com.projects.api_service.domain.NotificationRepository;

import tools.jackson.databind.ObjectMapper;

@Repository
public class NotificationPostgresRepository implements NotificationRepository {
    private final JdbcTemplate template;
    private final ObjectMapper objectMapper;

    public NotificationPostgresRepository(JdbcTemplate template, ObjectMapper objectMapper) {
        this.template = template;
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(Notification notification) {
        String sql = """
        INSERT INTO notifications (
            channel,
            recipient,
            template,
            payload,
            status
        ) values (?,?,?,?,?)
                """;

        String payload = objectMapper.writeValueAsString(notification.getPayload());

        template.update(
            sql,
            notification.getChannel().name(), 
            notification.getRecipient(), 
            notification.getTemplateId(), 
            payload,
            notification.getStatus().name() 
        );
    }
    
    
}
