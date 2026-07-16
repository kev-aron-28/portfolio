package com.projects.message_worker.infra.storage;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.projects.message_worker.domain.Notification;
import com.projects.message_worker.domain.NotificationChannel;
import com.projects.message_worker.domain.NotificationRepository;
import com.projects.message_worker.domain.NotificationStatus;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
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
    public void update(Notification notification) {
        String sql = """
            UPDATE notifications
            SET
                status = ?,
                retry_count = ?,
                sent_at = ?,
                delivered_at = ?,
                failed_at = ?,
                failure_reason = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
            """;

        template.update(
                sql,
                notification.getStatus().name(),
                notification.getRetryCount(),
                notification.getSentAt(),
                notification.getDeliveredAt(),
                notification.getFailedAt(),
                notification.getFailureReason(),
                notification.getId()
        );
    }

    @Override
    public Optional<Notification> findById(Long id) {
        String sql = """
        SELECT
            id,
            channel,
            recipient,
            template,
            payload,
            status,
            retry_count,
            idempotency_key,
            schedule_at,
            sent_at,
            delivered_at,
            failed_at,
            failure_reason,
            created_at,
            updated_at
        FROM notifications
        WHERE id = ?
        """;

        try {

            Notification notification = this.template.queryForObject(sql, (rs, rowNumber) -> {
                Map<String, Object> payload;

                String payloadJson = rs.getString("payload");

                if (payloadJson == null) {
                    payload = Map.of();
                } else {
                    payload = objectMapper.readValue(
                            payloadJson,
                            new TypeReference<Map<String, Object>>() {
                    }
                    );
                }

                return Notification.fromPrimitives(
                        rs.getLong("id"),
                        rs.getLong("template"),
                        NotificationStatus.valueOf(rs.getString("status")),
                        NotificationChannel.valueOf(rs.getString("channel")),
                        payload,
                        rs.getString("recipient"),
                        rs.getInt("retry_count"),
                        rs.getString("idempotency_key"),
                        rs.getTimestamp("schedule_at") == null ? null : rs.getTimestamp("schedule_at").toLocalDateTime(),
                        rs.getTimestamp("sent_at") == null ? null : rs.getTimestamp("sent_at").toLocalDateTime(),
                        rs.getTimestamp("delivered_at") == null ? null : rs.getTimestamp("delivered_at").toLocalDateTime(),
                        rs.getTimestamp("failed_at") == null ? null : rs.getTimestamp("failed_at").toLocalDateTime(),
                        rs.getString("failure_reason"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("updated_at").toLocalDateTime()
                );
            }, id);

            return Optional.of(notification);

        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (JacksonException e) {
            throw new RuntimeException("Unable to deserialize notification payload.", e);
        }
    }
}
