package com.projects.job_tracker.infrastructure.observatory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.projects.job_tracker.domain.exception.ResourceNotFoundException;
import com.projects.job_tracker.domain.observatory.*;

@Repository
public class JdbcMarketPostingRepository implements MarketPostingRepository {
    private final JdbcTemplate jdbc;
    public JdbcMarketPostingRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }
    public void insert(MarketPosting p) {
        insertCaptured(p,new CaptureModels.Observation(p.id(),p.id(),null,null,p.source(),p.url(),p.originalText(),EvidenceHash.sha256(p.originalText()),
            p.observedAt(),p.recordedAt(),CaptureModels.Availability.AVAILABLE,null),null);
    }
    public void insertCaptured(MarketPosting p, CaptureModels.Observation initial, String fingerprint) {
        jdbc.update("INSERT INTO observatory_postings (id, source, url, original_text, observed_at, recorded_at) VALUES (?, ?, ?, ?, ?, ?)",
            p.id(),p.source(),p.url(),p.originalText(),Timestamp.from(p.observedAt()),Timestamp.from(p.recordedAt()));
        jdbc.update("""
            INSERT INTO observatory_observations(id, posting_id, run_id, item_key, request_fingerprint, source, url, original_text,
                content_hash, observed_at, recorded_at, availability) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'AVAILABLE')
            """, initial.id(),p.id(),initial.runId(),initial.itemKey(),fingerprint,p.source(),p.url(),initial.originalText(),initial.contentHash(),
            Timestamp.from(initial.observedAt()),Timestamp.from(initial.recordedAt()));
        insertRevision(p.id(),p.current());
        jdbc.update("UPDATE observatory_observations SET data_revision = 1 WHERE id = ?",initial.id());
    }
    public Optional<MarketPosting> find(UUID id) {
        return jdbc.query("SELECT * FROM observatory_postings WHERE id = ?", this::posting, id).stream().findFirst();
    }
    public List<MarketPosting> list(int limit, int offset) {
        return jdbc.query("SELECT * FROM observatory_postings ORDER BY recorded_at DESC, id LIMIT ? OFFSET ?", this::posting, limit, offset);
    }
    public void lock(UUID id) {
        var rows = jdbc.queryForList("SELECT id FROM observatory_postings WHERE id = ? FOR UPDATE", id);
        if (rows.isEmpty()) throw new ResourceNotFoundException("Anuncio del observatorio no encontrado");
    }
    public void append(UUID id, int expectedRevision, MarketPosting.Revision revision) {
        lock(id);
        Integer current = jdbc.queryForObject("SELECT MAX(revision) FROM observatory_posting_versions WHERE posting_id = ?", Integer.class, id);
        if (current == null || current != expectedRevision)
            throw new IllegalArgumentException("El anuncio cambió; recarga su versión antes de corregirlo");
        insertRevision(id, revision);
    }
    private void insertRevision(UUID id, MarketPosting.Revision r) {
        PostingData d = r.data();
        jdbc.update("""
            INSERT INTO observatory_posting_versions
            (posting_id, revision, recorded_at, reason, method, title, company, location, requirements,
             salary_min, salary_max, currency, period, tax_basis, requirements_reviewed, published_at, country_code, region, city, work_mode, weekly_hours, contract_type, seniority, remote_scope, remote_details, context_evidence, observation_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """, id, r.number(), Timestamp.from(r.recordedAt()), r.reason(), r.method(), d.title(), d.company(),
            d.location(), d.requirements(), d.salaryMin(), d.salaryMax(), d.currency(),
            d.period() == null ? null : d.period().name(), d.taxBasis() == null ? null : d.taxBasis().name(), d.requirementsReviewed(),
            d.context().publishedAt() == null ? null : Timestamp.from(d.context().publishedAt()),
            d.context().countryCode(), d.context().region(), d.context().city(), name(d.context().workMode()),
            d.context().weeklyHours(), name(d.context().contractType()), name(d.context().seniority()),
            name(d.context().remoteScope()), d.context().remoteDetails(), d.context().evidence(), r.observationId() == null ? id : r.observationId());
        for (SkillRequirement requirement : d.skillRequirements())
            jdbc.update("INSERT INTO observatory_posting_requirements(posting_id, posting_revision, skill_id, skill_revision, obligation, evidence) VALUES (?, ?, ?, ?, ?, ?)",
                id, r.number(), requirement.skillId(), requirement.skillRevision(), requirement.obligation().name(), requirement.evidence());
    }
    private List<SkillRequirement> readRequirements(UUID id, int revision) {
        return jdbc.query("SELECT * FROM observatory_posting_requirements WHERE posting_id = ? AND posting_revision = ? ORDER BY skill_id",
            (r, n) -> new SkillRequirement(r.getObject("skill_id", UUID.class), r.getInt("skill_revision"),
                SkillRequirement.Obligation.valueOf(r.getString("obligation")), r.getString("evidence")), id, revision);
    }
    private static String name(Enum<?> value) { return value == null ? null : value.name(); }
    private static <E extends Enum<E>> E enumValue(Class<E> type, String value) { return value == null ? null : Enum.valueOf(type, value); }
    private PostingContext readContext(ResultSet r) throws SQLException {
        Timestamp publication = r.getTimestamp("published_at");
        return new PostingContext(publication == null ? null : publication.toInstant(), r.getString("country_code"),
            r.getString("region"), r.getString("city"), enumValue(PostingContext.WorkMode.class, r.getString("work_mode")),
            r.getBigDecimal("weekly_hours"), enumValue(PostingContext.ContractType.class, r.getString("contract_type")),
            enumValue(PostingContext.Seniority.class, r.getString("seniority")), enumValue(PostingContext.RemoteScope.class, r.getString("remote_scope")),
            r.getString("remote_details"), r.getString("context_evidence"));
    }
    private MarketPosting posting(ResultSet rs, int row) throws SQLException {
        UUID id = rs.getObject("id", UUID.class);
        List<MarketPosting.Revision> revisions = jdbc.query(
            "SELECT * FROM observatory_posting_versions WHERE posting_id = ? ORDER BY revision", (v, n) ->
                new MarketPosting.Revision(v.getInt("revision"), v.getTimestamp("recorded_at").toInstant(),
                    v.getString("reason"), v.getString("method"), new PostingData(v.getString("title"),
                    v.getString("company"), v.getString("location"), v.getString("requirements"),
                    v.getBigDecimal("salary_min"), v.getBigDecimal("salary_max"), v.getString("currency"),
                    v.getString("period") == null ? null : PostingData.Period.valueOf(v.getString("period")),
                    v.getString("tax_basis") == null ? null : PostingData.TaxBasis.valueOf(v.getString("tax_basis")),
                    v.getBoolean("requirements_reviewed"), readRequirements(id, v.getInt("revision")), readContext(v)), v.getObject("observation_id", UUID.class)), id);
        return new MarketPosting(id, rs.getString("source"), rs.getString("url"), rs.getString("original_text"),
            rs.getTimestamp("observed_at").toInstant(), rs.getTimestamp("recorded_at").toInstant(), revisions,
            jdbc.query("SELECT * FROM observatory_observations WHERE posting_id = ? ORDER BY observed_at, recorded_at, id", JdbcCaptureRepository::mapObservation, id));
    }
}
