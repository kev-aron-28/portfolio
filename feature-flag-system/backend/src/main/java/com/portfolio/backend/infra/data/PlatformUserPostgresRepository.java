package com.portfolio.backend.infra.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.portfolio.backend.domain.PlatformUser;
import com.portfolio.backend.domain.PlatformUserRepository;

@Repository
public class PlatformUserPostgresRepository implements PlatformUserRepository {
    private final NamedParameterJdbcTemplate template;

    public PlatformUserPostgresRepository(NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    @Override
    public Optional<PlatformUser> findByUsername(String username) {
        try {
            String query = """
                    SELECT * FROM platform_user WHERE username = :username
                    """;
            
            PlatformUser user = template.queryForObject(
                query, 
                Map.of("username", username), 
                (rs, rowNum) -> new PlatformUser(
                    rs.getObject("id", UUID.class),
                    rs.getString("username"),
                    rs.getString("password")
                )
            );

            return Optional.of(user);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void register(PlatformUser user) {
        String sql = """
                INSERT INTO platform_user (id, username, password) 
                VALUES (:id, :username, :password)
                """;
        Map<String, Object> params = new HashMap<>();
        params.put("username", user.getUsername());
        params.put("id", user.getId());
        params.put("password", user.getPassword());

        template.update(sql, params);
    }

    
}
