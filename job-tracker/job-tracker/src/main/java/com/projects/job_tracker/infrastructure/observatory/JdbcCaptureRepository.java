package com.projects.job_tracker.infrastructure.observatory;

import java.sql.*;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.projects.job_tracker.domain.observatory.*;
import com.projects.job_tracker.domain.observatory.CaptureModels.*;
import com.projects.job_tracker.domain.exception.ResourceNotFoundException;

@Repository
public class JdbcCaptureRepository implements CaptureRepository {
    private final JdbcTemplate jdbc;
    public JdbcCaptureRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }
    public List<SegmentVersion> segments() {
        return jdbc.query("SELECT v.* FROM observatory_segment_versions v WHERE revision = (SELECT MAX(s.revision) FROM observatory_segment_versions s WHERE s.segment_id = v.segment_id) ORDER BY name, segment_id", this::segment);
    }
    public List<SegmentVersion> segmentHistory(UUID id) {
        return jdbc.query("SELECT * FROM observatory_segment_versions WHERE segment_id = ? ORDER BY revision", this::segment, id);
    }
    public void saveSegment(SegmentVersion v, Integer expected) {
        if (expected == null) jdbc.update("INSERT INTO observatory_segments(id) VALUES (?)", v.id());
        else checkRevision("observatory_segments", "observatory_segment_versions", "segment_id", v.id(), expected);
        var s = v.spec();
        jdbc.update("INSERT INTO observatory_segment_versions(segment_id, revision, name, description, role_family, country_code, seniority, work_mode, reason, recorded_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            v.id(), v.revision(), s.name(), s.description(), s.roleFamily(), s.countryCode(), name(s.seniority()), name(s.workMode()), v.reason(), Timestamp.from(v.recordedAt()));
    }
    public List<ProtocolVersion> protocols() {
        return jdbc.query("SELECT v.* FROM observatory_protocol_versions v WHERE revision = (SELECT MAX(s.revision) FROM observatory_protocol_versions s WHERE s.protocol_id = v.protocol_id) ORDER BY name, protocol_id", this::protocol);
    }
    public List<ProtocolVersion> protocolHistory(UUID id) {
        return jdbc.query("SELECT * FROM observatory_protocol_versions WHERE protocol_id = ? ORDER BY revision", this::protocol, id);
    }
    public void saveProtocol(ProtocolVersion v, Integer expected) {
        if (expected == null) jdbc.update("INSERT INTO observatory_protocols(id) VALUES (?)", v.id());
        else checkRevision("observatory_protocols", "observatory_protocol_versions", "protocol_id", v.id(), expected);
        var s = v.spec();
        jdbc.update("""
            INSERT INTO observatory_protocol_versions(protocol_id, revision, segment_id, segment_revision, name, source,
                query_text, filters, interval_minutes, result_limit, ordering_rule, pagination, sampling_mode, reason, recorded_at, collector_platform, collector_location, collector_work_mode, collector_posted_days, collector_max_pages)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """, v.id(), v.revision(), s.segmentId(), s.segmentRevision(), s.name(), s.source(), s.query(), s.filters(),
            s.intervalMinutes(), s.resultLimit(), s.ordering(), s.pagination(), s.samplingMode().name(),v.reason(),Timestamp.from(v.recordedAt()),
            s.collectionPlan()==null ? null : s.collectionPlan().platform().name(),s.collectionPlan()==null ? null : s.collectionPlan().location(),
            s.collectionPlan()==null ? null : name(s.collectionPlan().workMode()),s.collectionPlan()==null ? null : s.collectionPlan().postedWithinDays(),s.collectionPlan()==null ? null : s.collectionPlan().maxPages());
    }
    // All dynamic table/column identifiers below are internal constants, never request input.
    private void checkRevision(String root, String versions, String fk, UUID id, int expected) {
        lockRoot(root, id);
        Integer current = jdbc.queryForObject("SELECT MAX(revision) FROM " + versions + " WHERE " + fk + " = ?", Integer.class, id);
        if (current == null || current != expected) throw new IllegalArgumentException("La configuración cambió; recarga su versión");
    }
    private void lockRoot(String table, UUID id) {
        if (jdbc.queryForList("SELECT id FROM " + table + " WHERE id = ? FOR UPDATE", id).isEmpty())
            throw new ResourceNotFoundException("Configuración no encontrada");
    }
    public void lockProtocol(UUID id) { lockRoot("observatory_protocols", id); }
    public Optional<CaptureRun> findRunByKey(UUID protocolId, UUID key) {
        return jdbc.query("SELECT * FROM observatory_capture_runs WHERE protocol_id = ? AND request_key = ?", this::run, protocolId, key).stream().findFirst();
    }
    public void insertRun(CaptureRun r) {
        jdbc.update("INSERT INTO observatory_capture_runs(id, protocol_id, protocol_revision, request_key, started_at, state) VALUES (?, ?, ?, ?, ?, 'RUNNING')",
            r.id(), r.protocolId(), r.protocolRevision(), r.requestKey(), Timestamp.from(r.startedAt()));
    }
    public CaptureRun lockRun(UUID id) {
        if (jdbc.queryForList("SELECT id FROM observatory_capture_runs WHERE id = ? FOR UPDATE", id).isEmpty())
            throw new ResourceNotFoundException("Ejecución no encontrada");
        return findRun(id).orElseThrow();
    }
    public Optional<CaptureRun> findRun(UUID id) {
        return jdbc.query("SELECT * FROM observatory_capture_runs WHERE id = ?", this::run, id).stream().findFirst();
    }
    public List<CaptureRun> runs(int limit, int offset) {
        return jdbc.query("SELECT * FROM observatory_capture_runs ORDER BY started_at DESC, id LIMIT ? OFFSET ?", this::run, limit, offset);
    }
    public void finish(CaptureRun r) {
        jdbc.update("UPDATE observatory_capture_runs SET finished_at = ?, state = ?, pages_visited = ?, truncated = ?, notes = ? WHERE id = ?",
            Timestamp.from(r.finishedAt()), r.state().name(), r.pagesVisited(), r.truncated(), r.notes(), r.id());
    }
    public Optional<Observation> findItem(UUID runId, String itemKey, String fingerprint) {
        List<String> keys = jdbc.queryForList("SELECT request_fingerprint FROM observatory_observations WHERE run_id = ? AND item_key = ?", String.class, runId, itemKey);
        if (keys.isEmpty()) return Optional.empty();
        if (!fingerprint.equals(keys.getFirst())) throw new IllegalArgumentException("La clave del elemento ya se utilizó con otros datos");
        return jdbc.query("SELECT * FROM observatory_observations WHERE run_id = ? AND item_key = ?", JdbcCaptureRepository::mapObservation, runId, itemKey).stream().findFirst();
    }
    public void insertObservation(Observation o, String fingerprint) {
        jdbc.update("""
            INSERT INTO observatory_observations(id, posting_id, run_id, item_key, request_fingerprint, source, url,
                original_text, content_hash, observed_at, recorded_at, availability, data_revision)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """, o.id(), o.postingId(), o.runId(), o.itemKey(), fingerprint, o.source(), o.url(), o.originalText(), o.contentHash(),
            Timestamp.from(o.observedAt()), Timestamp.from(o.recordedAt()), o.availability().name(), o.dataRevision());
    }
    public void linkRevision(UUID id, int revision) {
        jdbc.update("UPDATE observatory_observations SET data_revision = ? WHERE id = ?", revision, id);
    }
    public List<Observation> runObservations(UUID runId) {
        return jdbc.query("SELECT * FROM observatory_observations WHERE run_id = ? ORDER BY observed_at, id", JdbcCaptureRepository::mapObservation, runId);
    }
    public static Observation mapObservation(ResultSet r, int n) throws SQLException {
        String original = r.getString("original_text");
        String hash = r.getString("content_hash");
        return new Observation(r.getObject("id", UUID.class), r.getObject("posting_id", UUID.class), r.getObject("run_id", UUID.class),
            r.getString("item_key"), r.getString("source"), r.getString("url"), original,
            hash == null && original != null ? EvidenceHash.sha256(original) : hash,
            r.getTimestamp("observed_at").toInstant(), r.getTimestamp("recorded_at").toInstant(),
            Availability.valueOf(r.getString("availability")), r.getObject("data_revision", Integer.class));
    }
    private SegmentVersion segment(ResultSet r, int n) throws SQLException {
        return new SegmentVersion(r.getObject("segment_id", UUID.class), r.getInt("revision"), new SegmentSpec(r.getString("name"),
            r.getString("description"), r.getString("role_family"), r.getString("country_code"),
            value(PostingContext.Seniority.class, r.getString("seniority")), value(PostingContext.WorkMode.class, r.getString("work_mode"))),
            r.getString("reason"), r.getTimestamp("recorded_at").toInstant());
    }
    private ProtocolVersion protocol(ResultSet r, int n) throws SQLException {
        return new ProtocolVersion(r.getObject("protocol_id", UUID.class), r.getInt("revision"), new ProtocolSpec(r.getObject("segment_id", UUID.class),
            r.getInt("segment_revision"), r.getString("name"), r.getString("source"), r.getString("query_text"), r.getString("filters"),
            r.getInt("interval_minutes"), r.getInt("result_limit"), r.getString("ordering_rule"), r.getString("pagination"), SamplingMode.valueOf(r.getString("sampling_mode")),r.getString("collector_platform")==null ? null : new CollectionPlan(
                com.projects.job_tracker.domain.model.JobPlatform.valueOf(r.getString("collector_platform")),r.getString("collector_location"),
                value(PostingContext.WorkMode.class,r.getString("collector_work_mode")),r.getObject("collector_posted_days",Integer.class),r.getObject("collector_max_pages",Integer.class))),
            r.getString("reason"), r.getTimestamp("recorded_at").toInstant());
    }
    private CaptureRun run(ResultSet r, int n) throws SQLException {
        UUID id = r.getObject("id", UUID.class);
        Timestamp finished = r.getTimestamp("finished_at");
        return new CaptureRun(id, r.getObject("protocol_id", UUID.class), r.getInt("protocol_revision"), r.getObject("request_key", UUID.class),
            r.getTimestamp("started_at").toInstant(), finished == null ? null : finished.toInstant(), RunState.valueOf(r.getString("state")),
            r.getObject("pages_visited", Integer.class), r.getBoolean("truncated"), r.getString("notes"),
            jdbc.queryForObject("SELECT COUNT(*) FROM observatory_observations WHERE run_id = ?", Long.class, id));
    }
    private static String name(Enum<?> value) { return value == null ? null : value.name(); }
    private static <E extends Enum<E>> E value(Class<E> type, String value) { return value == null ? null : Enum.valueOf(type, value); }
}
