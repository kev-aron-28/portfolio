package com.projects.job_tracker.infrastructure.observatory;

import java.sql.*;
import java.time.Instant;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.projects.job_tracker.domain.observatory.DuplicateRepository;
import com.projects.job_tracker.domain.observatory.DuplicateModels.*;

@Repository
public class JdbcDuplicateRepository implements DuplicateRepository {
    private final JdbcTemplate jdbc;
    public JdbcDuplicateRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }
    public void lockRegistry() { jdbc.queryForList("SELECT id FROM observatory_identity_registry WHERE id = 1 FOR UPDATE"); }
    public List<SourceClaim> claimsBySource(String source) {
        return jdbc.query("SELECT * FROM observatory_source_postings WHERE source_key = ? ORDER BY posting_id",this::mapClaim,source);
    }
    public Optional<SourceClaim> claim(UUID id) {
        return jdbc.query("SELECT * FROM observatory_source_postings WHERE posting_id = ?",this::mapClaim,id).stream().findFirst();
    }
    public void attach(SourceClaim claim) {
        jdbc.update("INSERT INTO observatory_source_postings(posting_id, source_key, url, external_id, registration_note) VALUES (?, ?, ?, ?, ?)",
            claim.postingId(),claim.sourceKey(),claim.url(),claim.externalId(),claim.registrationNote());
    }
    public List<Snapshot> snapshots() {
        return jdbc.query("""
            SELECT p.id, p.source, p.url, p.recorded_at, v.revision, v.title, v.company, s.external_id,
                o.content_hash, o.original_text
            FROM observatory_postings p
            JOIN observatory_source_postings s ON s.posting_id = p.id
            JOIN observatory_posting_versions v ON v.posting_id = p.id
            JOIN observatory_observations o ON o.id = v.observation_id
            WHERE v.revision = (SELECT MAX(w.revision) FROM observatory_posting_versions w WHERE w.posting_id = p.id)
            ORDER BY p.id
            """,(r,n) -> new Snapshot(r.getObject("id",UUID.class),r.getInt("revision"),r.getString("title"),r.getString("company"),
                r.getString("source"),r.getString("url"),r.getString("external_id"),r.getString("content_hash"),r.getString("original_text"),r.getTimestamp("recorded_at").toInstant()));
    }
    public List<Review> currentReviews(Instant cutoff) {
        return jdbc.query("""
            SELECT p.id, p.left_id, p.right_id, v.revision, v.decision, v.reason,
                   v.left_revision, v.right_revision, v.recorded_at, v.request_hash
            FROM observatory_duplicate_pairs p JOIN observatory_duplicate_reviews v ON v.pair_id = p.id
            WHERE v.revision = (SELECT MAX(w.revision) FROM observatory_duplicate_reviews w
                                WHERE w.pair_id = p.id AND w.recorded_at <= ?)
            ORDER BY p.left_id, p.right_id
            """,this::review,Timestamp.from(cutoff));
    }
    public List<Review> history(UUID id) {
        return jdbc.query("""
            SELECT p.id, p.left_id, p.right_id, v.revision, v.decision, v.reason,
                   v.left_revision, v.right_revision, v.recorded_at, v.request_hash
            FROM observatory_duplicate_pairs p JOIN observatory_duplicate_reviews v ON v.pair_id = p.id
            WHERE p.id = ? ORDER BY v.revision
            """,this::review,id);
    }
    public void save(Review r, boolean newPair) {
        if (newPair) jdbc.update("INSERT INTO observatory_duplicate_pairs(id,left_id,right_id) VALUES (?, ?, ?)",r.id(),r.leftId(),r.rightId());
        jdbc.update("""
            INSERT INTO observatory_duplicate_reviews(pair_id, revision, decision, reason, left_id, left_revision,
                right_id, right_revision, recorded_at, request_hash) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """,r.id(),r.revision(),r.decision().name(),r.reason(),r.leftId(),r.leftRevision(),r.rightId(),r.rightRevision(),Timestamp.from(r.recordedAt()),r.requestHash());
    }
    private SourceClaim mapClaim(ResultSet r, int n) throws SQLException {
        return new SourceClaim(r.getObject("posting_id",UUID.class),r.getString("source_key"),r.getString("url"),r.getString("external_id"),r.getString("registration_note"));
    }
    private Review review(ResultSet r, int n) throws SQLException {
        return new Review(r.getObject("id",UUID.class),r.getObject("left_id",UUID.class),r.getObject("right_id",UUID.class),r.getInt("revision"),
            Decision.valueOf(r.getString("decision")),r.getString("reason"),r.getInt("left_revision"),r.getInt("right_revision"),r.getTimestamp("recorded_at").toInstant(),r.getString("request_hash"));
    }
}
