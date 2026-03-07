package com.portfolio.backend.infra.data;

import java.util.Map;
import java.util.UUID;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.portfolio.backend.domain.FeatureFlag;
import com.portfolio.backend.domain.FeatureFlagRepository;

@Repository
public class PostgresRepository implements FeatureFlagRepository {

    private NamedParameterJdbcTemplate template;

    public PostgresRepository(NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    @Override
    public void save(FeatureFlag featureFlag) {
        if(exists(featureFlag.getId())) {
            updateFeatureFlag(featureFlag);
        } else {
            insertFeatureFlag(featureFlag);
        }

        deleteRoles(featureFlag.getId());
        deleteUsers(featureFlag.getId());

        insertRoles(featureFlag);
        insertUsers(featureFlag);
    }

    @Override
    public boolean exists(UUID id) {
        String sql = """
                SELECT COUNT(*)
                FROM feature_flags
                WHERE id = :id
                """;

        Integer count = template.queryForObject(
            sql, 
            Map.of("id", id), 
            Integer.class
        );

        return count != null && count > 0;
    }

    private void insertFeatureFlag(FeatureFlag flag) {
        String sql = """
            INSERT INTO feature_flags (
                id, 
                flag_key, 
                description,
                enabled,
                rollout_percentage,
                created_at,
                updated_at
            ) VALUES (
                :id,
                :flag_key,
                :description,
                :enabled,
                :rollout_percentage,
                :created_at,
                :updated_at 
            )
        """;

        Map<String, Object> params = Map.of(
            "id", flag.getId(),
            "flag_key", flag.getKey(),
            "description", flag.getDescription(),
            "enabled", flag.isEnabled(),
            "rollout_percentage", flag.getRolloutPercentage(),
            "created_at", flag.getCreatedAt(),
            "updated_at", flag.getUpdatedAt()
        );

        template.update(sql, params);
    }
    
    private void updateFeatureFlag(FeatureFlag flag) {
        String sql = """
            UPDATE feature_flags
            SET description            = :description,
                enabled                = :enabled,
                rollout_percentage     = :rollout_percentage,
                updated_at             = :updated_at
            WHERE id = :id
        """;

        Map<String, Object> params = Map.of(
            "id", flag.getId(),
            "description", flag.getDescription(),
            "enabled", flag.isEnabled(),
            "rollout_percentage", flag.getRolloutPercentage(),
            "updated_at", flag.getUpdatedAt()
        );

        template.update(sql, params);
    }


    private void deleteRoles(UUID flagId) {
        template.update("""
            DELETE FROM feature_flag_roles
            WHERE feature_flag_id = :id
                """,
            Map.of("id", flagId));
    }

    private void deleteUsers(UUID flagId) {
        template.update("""
            DELETE FROM feature_flag_users
            WHERE feature_flag_id = :id   
                """,
            Map.of("id", flagId));
    }


    private void insertRoles(FeatureFlag flag) {
        if (flag.getAllowedRoles().isEmpty()) return;

        String sql = """
            INSERT INTO feature_flag_roles (feature_flag_id, role)
            VALUES (:flagId, :role)
        """;

        SqlParameterSource[] batch = flag.getAllowedRoles().stream()
                .map(role -> new MapSqlParameterSource()
                        .addValue("flagId", flag.getId())
                        .addValue("role", role.getName()))
                .toArray(SqlParameterSource[]::new);

        template.batchUpdate(sql, batch);
    }

    private void insertUsers(FeatureFlag flag) {
        if (flag.getAllowedUsers().isEmpty()) return;

        String sql = """
            INSERT INTO feature_flag_users (feature_flag_id, user_id)
            VALUES (:flagId, :userId)
        """;

        SqlParameterSource[] batch = flag.getAllowedUsers().stream()
                .map(user -> new MapSqlParameterSource()
                        .addValue("flagId", flag.getId())
                        .addValue("userId", user.getUserId()))
                .toArray(SqlParameterSource[]::new);

        template.batchUpdate(sql, batch);
    }
    
}
