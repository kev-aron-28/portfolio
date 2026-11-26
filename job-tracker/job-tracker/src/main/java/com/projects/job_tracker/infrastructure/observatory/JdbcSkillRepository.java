package com.projects.job_tracker.infrastructure.observatory;

import java.sql.*;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.projects.job_tracker.domain.observatory.*;
import com.projects.job_tracker.domain.exception.ResourceNotFoundException;

@Repository
public class JdbcSkillRepository implements SkillRepository {
    private final JdbcTemplate jdbc;
    public JdbcSkillRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }
    public List<SkillVersion> list() {
        return jdbc.query("SELECT v.* FROM observatory_skill_versions v WHERE revision = (SELECT MAX(s.revision) FROM observatory_skill_versions s WHERE s.skill_id = v.skill_id) ORDER BY name, skill_id", this::map);
    }
    public List<SkillVersion> history(UUID id) {
        return jdbc.query("SELECT * FROM observatory_skill_versions WHERE skill_id = ? ORDER BY revision", this::map, id);
    }
    public void create(SkillVersion skill) {
        jdbc.update("INSERT INTO observatory_skills(id) VALUES (?)", skill.id());
        insert(skill);
    }
    public void append(SkillVersion skill, int expectedRevision) {
        if (jdbc.queryForList("SELECT id FROM observatory_skills WHERE id = ? FOR UPDATE", skill.id()).isEmpty())
            throw new ResourceNotFoundException("Habilidad no encontrada");
        Integer revision = jdbc.queryForObject("SELECT MAX(revision) FROM observatory_skill_versions WHERE skill_id = ?", Integer.class, skill.id());
        if (revision == null || revision != expectedRevision) throw new IllegalArgumentException("El catálogo cambió; recarga antes de editar");
        insert(skill);
    }
    private void insert(SkillVersion s) {
        jdbc.update("INSERT INTO observatory_skill_versions(skill_id, revision, name, reason, recorded_at) VALUES (?, ?, ?, ?, ?)",
            s.id(), s.revision(), s.name(), s.reason(), Timestamp.from(s.recordedAt()));
        for (String alias : s.aliases()) jdbc.update("INSERT INTO observatory_skill_aliases(skill_id, revision, alias) VALUES (?, ?, ?)", s.id(), s.revision(), alias);
    }
    private SkillVersion map(ResultSet r, int row) throws SQLException {
        UUID id = r.getObject("skill_id", UUID.class);
        int revision = r.getInt("revision");
        return new SkillVersion(id, revision, r.getString("name"),
            jdbc.queryForList("SELECT alias FROM observatory_skill_aliases WHERE skill_id = ? AND revision = ? ORDER BY alias", String.class, id, revision),
            r.getString("reason"), r.getTimestamp("recorded_at").toInstant());
    }
}
