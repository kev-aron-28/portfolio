package com.projects.job_tracker.domain.observatory;

import java.time.Instant;
import java.util.*;
import com.projects.job_tracker.domain.observatory.ScheduleModels.*;

public interface ObservatoryScheduleRepository {
    void lockRegistry();
    List<Schedule> schedules();
    Optional<Schedule> schedule(UUID id);
    record Creation(Schedule schedule,String hash) {}
    Optional<Creation> byRequestKey(UUID requestKey);
    List<Version> history(UUID id);
    void save(Version version,UUID requestKey,String creationHash,Instant nextRunAt);
    List<Schedule> due(Instant now);
    void activate(UUID scheduleId,UUID taskId,Instant nextRunAt);
    void release(UUID scheduleId,UUID taskId,UUID lastRunId,int failures);
    List<Task> pending(UUID scheduleId);
    List<Task> activeTasks();
    Optional<Task> task(UUID id,boolean lock);
    List<Task> tasks(UUID scheduleId,int limit,int offset);
    void insert(Task task);
    void update(Task task);
    void heartbeat(UUID taskId,UUID token,Instant leaseUntil,Instant hardBefore);
}
