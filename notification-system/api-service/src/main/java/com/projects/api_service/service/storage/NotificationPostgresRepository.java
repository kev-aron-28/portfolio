package com.projects.api_service.service.storage;

import java.util.UUID;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.projects.api_service.domain.Notification;
import com.projects.api_service.domain.NotificationRepository;

import tools.jackson.core.JacksonException;
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
    public Long save(Notification notification) {
        String sql = """
        INSERT INTO notifications (
            channel,
            recipient,
            template,
            payload,
            status,
            idempotency_key
        ) values (?,?,?,CAST(? as JSONB),?, ?)
         RETURNING id
                """;

        try {
            String payload = objectMapper.writeValueAsString(notification.getPayload());

            return template.queryForObject(
                sql,
                Long.class,
                notification.getChannel().name(), 
                notification.getRecipient(), 
                notification.getTemplateId(), 
                payload,
                notification.getStatus().name(),
                UUID.randomUUID().toString()
            );
        } catch (DataAccessException | JacksonException e) {
            System.out.println(e);
            throw new RuntimeException("Failed to serialized payload");
        }
    }
    
    
}
