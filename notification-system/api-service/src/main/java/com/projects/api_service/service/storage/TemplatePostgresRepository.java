package com.projects.api_service.service.storage;

import java.sql.Types;
import java.util.Optional;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.projects.api_service.domain.NotificationChannel;
import com.projects.api_service.domain.Template;
import com.projects.api_service.domain.TemplateRepository;
import com.projects.api_service.domain.errors.TemplateAlreadyExists;

@Repository
public class TemplatePostgresRepository implements TemplateRepository {

    private final JdbcTemplate jdbcTemplate;

    public TemplatePostgresRepository(JdbcTemplate template) {
        this.jdbcTemplate = template;
    }
    
    @Override
    public void save(Template template) {
        String sql = """
                INSERT INTO templates (
                    name,
                    channel,
                    content
                ) values (?,?,?)
                """;

        try {
            jdbcTemplate.update(
                sql, 
                new Object[] { template.getName(), template.getChannel().name(), template.getContent() },
                new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR }
            );
        } catch (DuplicateKeyException e) {
            throw new TemplateAlreadyExists();
        }
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
