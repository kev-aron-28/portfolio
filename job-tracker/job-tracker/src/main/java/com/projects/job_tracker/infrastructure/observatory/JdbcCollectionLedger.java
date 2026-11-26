package com.projects.job_tracker.infrastructure.observatory;

import java.sql.*;
import java.time.Instant;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.projects.job_tracker.domain.observatory.*;

@Repository
public class JdbcCollectionLedger implements CollectionLedger {
    private final JdbcTemplate jdbc;
    public JdbcCollectionLedger(JdbcTemplate jdbc) { this.jdbc=jdbc; }
    public Optional<Attempt> attempt(UUID id) { return readAttempt(id,false); }
    public Optional<Attempt> lockAttempt(UUID id) { return readAttempt(id,true); }
    private Optional<Attempt> readAttempt(UUID id,boolean lock) {
        return jdbc.query("SELECT * FROM observatory_collection_attempts WHERE run_id = ?"+(lock ? " FOR UPDATE" : ""),(r,n) -> new Attempt(id,r.getString("status"),r.getString("search_url"),
            r.getString("collector_version"),r.getTimestamp("started_at").toInstant(),r.getTimestamp("finished_at")==null ? null : r.getTimestamp("finished_at").toInstant(),r.getString("notes"),
            r.getTimestamp("heartbeat_at").toInstant(),r.getString("termination_kind")),id).stream().findFirst();
    }
    public void begin(UUID id,String url,String version) {
        var now=Timestamp.from(Instant.now());
        jdbc.update("INSERT INTO observatory_collection_attempts(run_id,status,search_url,collector_version,started_at,heartbeat_at) VALUES (?, 'RUNNING', ?, ?, ?, ?)",id,url,version,now,now);
    }
    public void finish(UUID id,String notes,String kind) {
        jdbc.update("UPDATE observatory_collection_attempts SET status = 'FINISHED', finished_at = ?, notes = ?, termination_kind = ? WHERE run_id = ? AND status = 'RUNNING'",Timestamp.from(Instant.now()),notes,kind,id);
    }
    public void heartbeat(UUID id,Instant at) {
        jdbc.update("UPDATE observatory_collection_attempts SET heartbeat_at = ? WHERE run_id = ? AND status = 'RUNNING'",Timestamp.from(at),id);
    }
    public List<UUID> staleAttempts(Instant heartbeatBefore,Instant startedBefore) {
        return jdbc.queryForList("SELECT run_id FROM observatory_collection_attempts WHERE status = 'RUNNING' AND (heartbeat_at <= ? OR started_at <= ?)",UUID.class,Timestamp.from(heartbeatBefore),Timestamp.from(startedBefore));
    }
    public void page(UUID runId,MarketCollector.PageEvidence page,String version) {
        jdbc.update("""
            INSERT INTO observatory_collection_pages(run_id,page_number,url,observed_at,html,card_count,fingerprint,next_url,outcome,collector_version)
            VALUES (?,?,?,?,?,?,?,?,?,?)
            """,runId,page.number(),page.url(),Timestamp.from(page.observedAt()),page.html(),page.cardCount(),page.fingerprint(),page.nextUrl(),page.outcome(),version);
    }
    public List<MarketCollector.PageEvidence> pages(UUID runId) {
        return jdbc.query("SELECT * FROM observatory_collection_pages WHERE run_id = ? ORDER BY page_number",(r,n)->new MarketCollector.PageEvidence(
            r.getInt("page_number"),r.getString("url"),r.getTimestamp("observed_at").toInstant(),r.getString("html"),r.getInt("card_count"),r.getString("fingerprint"),r.getString("next_url"),r.getString("outcome")),runId);
    }
    public RawItem stage(UUID runId,MarketCollector.Item item,String version) {
        var previous=jdbc.query("SELECT * FROM observatory_collected_items WHERE run_id = ? AND item_key = ?",this::map,runId,item.itemKey());
        if (!previous.isEmpty()) {
            var old=previous.getFirst();
            if (!Objects.equals(old.listingHtml(),item.listingHtml()) || !Objects.equals(old.detailHtml(),item.detailHtml()) || !Objects.equals(old.originalText(),item.originalText()) || !old.parserVersion().equals(version) || !old.observedAt().equals(item.observedAt()) || old.detailVerified()!=item.detailVerified())
                throw new IllegalArgumentException("La clave del elemento ya contiene otra evidencia");
            return old;
        }
        UUID id=UUID.randomUUID();
        jdbc.update("""
            INSERT INTO observatory_collected_items(id,run_id,item_key,observed_at,listing_html,detail_html,detail_verified,original_text,parser_version,status,issue)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'PENDING', ?)
            """,id,runId,item.itemKey(),Timestamp.from(item.observedAt()),item.listingHtml(),item.detailHtml(),item.detailVerified(),item.originalText(),version,item.issue());
        return jdbc.query("SELECT * FROM observatory_collected_items WHERE id = ?",this::map,id).getFirst();
    }
    public void imported(UUID id,UUID postingId,UUID observationId) {
        jdbc.update("UPDATE observatory_collected_items SET status = 'IMPORTED', posting_id = ?, observation_id = ? WHERE id = ?",postingId,observationId,id);
    }
    public void failed(UUID id,String issue) { jdbc.update("UPDATE observatory_collected_items SET status = 'ERROR', issue = ? WHERE id = ? AND status <> 'IMPORTED'",issue,id); }
    public List<RawItem> items(UUID runId) { return jdbc.query("SELECT * FROM observatory_collected_items WHERE run_id = ? ORDER BY observed_at,id",this::map,runId); }
    private RawItem map(ResultSet r,int n) throws SQLException {
        return new RawItem(r.getObject("id",UUID.class),r.getObject("run_id",UUID.class),r.getString("item_key"),r.getTimestamp("observed_at").toInstant(),r.getString("listing_html"),r.getString("detail_html"),r.getBoolean("detail_verified"),r.getString("original_text"),r.getString("parser_version"),r.getString("status"),r.getString("issue"),r.getObject("posting_id",UUID.class),r.getObject("observation_id",UUID.class));
    }
}
