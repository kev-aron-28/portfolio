package com.projects.message_worker.infra.storage;

import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.projects.message_worker.domain.NotificationChannel;
import com.projects.message_worker.domain.Template;
import com.projects.message_worker.domain.TemplateRepository;

@Repository
public class TemplatePostgresRepository implements TemplateRepository {

    private final JdbcTemplate jdbcTemplate;

    public TemplatePostgresRepository(JdbcTemplate template) {
        this.jdbcTemplate = template;
    }
    
    @Override
    public Optional<Template> findById(Long id) {
        String query = """
                SELECT
                    id,
                    name,
                    channel,
                    content
                FROM templates
                WHERE id = ?
                """;

        try {
            Template template = jdbcTemplate.queryForObject(
                query,
                (rs, rowNumber) -> {
                    Template t = new Template(
                        rs.getString("name"),
                        NotificationChannel.valueOf(rs.getString("channel")),
                        rs.getString("name"),
                        rs.getString("content")
                    );

                    t.setId(rs.getLong("id"));

                    return t;
                },
                id
            );
            
    
            return Optional.of(template);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    
    
}
