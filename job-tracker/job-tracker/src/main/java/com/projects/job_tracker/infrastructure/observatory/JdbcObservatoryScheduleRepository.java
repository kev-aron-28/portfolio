package com.projects.job_tracker.infrastructure.observatory;

import java.sql.*;
import java.time.Instant;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.projects.job_tracker.domain.observatory.ObservatoryScheduleRepository;
import com.projects.job_tracker.domain.observatory.ScheduleModels.*;

@Repository
public class JdbcObservatoryScheduleRepository implements ObservatoryScheduleRepository {
    private final JdbcTemplate jdbc;
    public JdbcObservatoryScheduleRepository(JdbcTemplate jdbc) { this.jdbc=jdbc; }
    private static final String CURRENT="SELECT v.*,s.next_run_at,s.active_task_id,s.last_run_id,s.consecutive_failures FROM observatory_schedules s JOIN observatory_schedule_versions v ON v.schedule_id=s.id AND v.revision=s.current_revision";
    public void lockRegistry() { jdbc.queryForList("SELECT id FROM observatory_schedule_registry WHERE id=1 FOR UPDATE"); }
    public List<Schedule> schedules() { return jdbc.query(CURRENT+" ORDER BY v.name,v.schedule_id",this::mapSchedule); }
    public Optional<Schedule> schedule(UUID id) { return jdbc.query(CURRENT+" WHERE s.id=?",this::mapSchedule,id).stream().findFirst(); }
    public Optional<Creation> byRequestKey(UUID key) {
        var ids=jdbc.queryForList("SELECT id FROM observatory_schedules WHERE request_key=?",UUID.class,key);
        if (ids.isEmpty()) return Optional.empty();
        String stored=jdbc.queryForObject("SELECT creation_hash FROM observatory_schedules WHERE id=?",String.class,ids.getFirst());
        return Optional.of(new Creation(schedule(ids.getFirst()).orElseThrow(),stored));
    }
    public List<Version> history(UUID id) { return jdbc.query("SELECT * FROM observatory_schedule_versions WHERE schedule_id=? ORDER BY revision",this::mapVersion,id); }
    public void save(Version v,UUID requestKey,String hash,Instant nextRunAt) {
        var c=v.config();
        if (v.revision()==1) jdbc.update("INSERT INTO observatory_schedules(id,current_revision,enabled,request_key,creation_hash,next_run_at) VALUES (?,1,?,?,?,?)",v.id(),c.enabled(),requestKey,hash,ts(nextRunAt));
        else jdbc.update("UPDATE observatory_schedules SET current_revision=?,enabled=?,next_run_at=?,consecutive_failures=0 WHERE id=?",v.revision(),c.enabled(),ts(nextRunAt),v.id());
        jdbc.update("""
            INSERT INTO observatory_schedule_versions(schedule_id,revision,name,protocol_id,protocol_revision,enabled,start_at,
                interval_minutes,max_retries,backoff_minutes,reason,recorded_at) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)
            """,v.id(),v.revision(),c.name(),c.protocolId(),c.protocolRevision(),c.enabled(),ts(c.startAt()),v.intervalMinutes(),c.maxRetries(),c.backoffMinutes(),v.reason(),ts(v.recordedAt()));
    }
    public List<Schedule> due(Instant now) {
        return jdbc.query(CURRENT+" WHERE s.enabled=TRUE AND s.active_task_id IS NULL AND (s.next_run_at<=? OR EXISTS (SELECT 1 FROM observatory_schedule_tasks t WHERE t.schedule_id=s.id AND t.state='PENDING' AND t.due_at<=?)) ORDER BY s.next_run_at,s.id",this::mapSchedule,ts(now),ts(now));
    }
    public void activate(UUID id,UUID task,Instant next) { jdbc.update("UPDATE observatory_schedules SET active_task_id=?,next_run_at=? WHERE id=?",task,ts(next),id); }
    public void release(UUID id,UUID task,UUID run,int failures) { jdbc.update("UPDATE observatory_schedules SET active_task_id=NULL,last_run_id=COALESCE(?,last_run_id),consecutive_failures=? WHERE id=? AND active_task_id=?",run,failures,id,task); }
    public List<Task> pending(UUID id) { return jdbc.query("SELECT * FROM observatory_schedule_tasks WHERE schedule_id=? AND state='PENDING' ORDER BY due_at,id",this::mapTask,id); }
    public List<Task> activeTasks() { return jdbc.query("SELECT * FROM observatory_schedule_tasks WHERE state IN ('CLAIMED','RUNNING') ORDER BY claimed_at,id",this::mapTask); }
    public Optional<Task> task(UUID id,boolean lock) { return jdbc.query("SELECT * FROM observatory_schedule_tasks WHERE id=?"+(lock ? " FOR UPDATE" : ""),this::mapTask,id).stream().findFirst(); }
    public List<Task> tasks(UUID id,int limit,int offset) { return jdbc.query("SELECT * FROM observatory_schedule_tasks WHERE schedule_id=? ORDER BY due_at DESC,id LIMIT ? OFFSET ?",this::mapTask,id,limit,offset); }
    public void insert(Task t) {
        jdbc.update("""
            INSERT INTO observatory_schedule_tasks(id,schedule_id,schedule_revision,scheduled_for,due_at,retry_number,retry_of,
                state,lease_token,lease_until,claimed_at,run_id,finished_at,notes,skipped_slots) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            """,t.id(),t.scheduleId(),t.scheduleRevision(),ts(t.scheduledFor()),ts(t.dueAt()),t.retryNumber(),t.retryOf(),t.state().name(),t.leaseToken(),ts(t.leaseUntil()),ts(t.claimedAt()),t.runId(),ts(t.finishedAt()),t.notes(),t.skippedSlots());
    }
    public void update(Task t) { jdbc.update("UPDATE observatory_schedule_tasks SET state=?,lease_token=?,lease_until=?,claimed_at=?,run_id=?,finished_at=?,notes=? WHERE id=?",t.state().name(),t.leaseToken(),ts(t.leaseUntil()),ts(t.claimedAt()),t.runId(),ts(t.finishedAt()),t.notes(),t.id()); }
    public void heartbeat(UUID id,UUID token,Instant leaseUntil,Instant hardBefore) {
        jdbc.update("""
            UPDATE observatory_schedule_tasks SET lease_until=? WHERE id=? AND lease_token=? AND state IN ('CLAIMED','RUNNING')
              AND claimed_at>? AND (run_id IS NULL OR EXISTS (SELECT 1 FROM observatory_capture_runs r WHERE r.id=run_id AND r.state='RUNNING'))
            """,ts(leaseUntil),id,token,ts(hardBefore));
    }
    private Schedule mapSchedule(ResultSet r,int n) throws SQLException {
        return new Schedule(mapVersion(r,n),r.getTimestamp("next_run_at").toInstant(),r.getObject("active_task_id",UUID.class),r.getObject("last_run_id",UUID.class),r.getInt("consecutive_failures"));
    }
    private Version mapVersion(ResultSet r,int n) throws SQLException {
        return new Version(r.getObject("schedule_id",UUID.class),r.getInt("revision"),new Config(r.getString("name"),r.getObject("protocol_id",UUID.class),r.getInt("protocol_revision"),r.getBoolean("enabled"),r.getTimestamp("start_at").toInstant(),r.getInt("max_retries"),r.getInt("backoff_minutes")),r.getInt("interval_minutes"),r.getString("reason"),r.getTimestamp("recorded_at").toInstant());
    }
    private Task mapTask(ResultSet r,int n) throws SQLException {
        return new Task(r.getObject("id",UUID.class),r.getObject("schedule_id",UUID.class),r.getInt("schedule_revision"),at(r,"scheduled_for"),at(r,"due_at"),r.getInt("retry_number"),r.getObject("retry_of",UUID.class),TaskState.valueOf(r.getString("state")),r.getObject("lease_token",UUID.class),at(r,"lease_until"),at(r,"claimed_at"),r.getObject("run_id",UUID.class),at(r,"finished_at"),r.getString("notes"),r.getLong("skipped_slots"));
    }
    private static Timestamp ts(Instant at) { return at==null ? null : Timestamp.from(at); }
    private static Instant at(ResultSet r,String key) throws SQLException { var value=r.getTimestamp(key); return value==null ? null : value.toInstant(); }
}
