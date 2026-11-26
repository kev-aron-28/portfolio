package com.projects.job_tracker.domain.observatory;

import java.time.Instant;
import java.util.*;

public final class ScheduleModels {
    private ScheduleModels() {}
    public enum TaskState { PENDING, CLAIMED, RUNNING, SUCCEEDED, FAILED, ABANDONED, CANCELLED }
    public record Config(String name,UUID protocolId,Integer protocolRevision,Boolean enabled,Instant startAt,Integer maxRetries,Integer backoffMinutes) {
        public Config {
            name=CaptureModels.required(name,"Nombre de programación",200);
            if (protocolId==null || protocolRevision==null || protocolRevision<1 || enabled==null || startAt==null)
                throw new IllegalArgumentException("Indica protocolo, versión, inicio y activación");
            startAt=startAt.truncatedTo(java.time.temporal.ChronoUnit.MICROS);
            if (maxRetries==null || maxRetries<0 || maxRetries>5 || backoffMinutes==null || backoffMinutes<1 || backoffMinutes>1440)
                throw new IllegalArgumentException("Reintentos entre 0 y 5; espera entre 1 y 1440 minutos");
        }
    }
    public record Version(UUID id,int revision,Config config,int intervalMinutes,String reason,Instant recordedAt) {}
    public record Schedule(Version version,Instant nextRunAt,UUID activeTaskId,UUID lastRunId,int consecutiveFailures) {}
    public record Task(UUID id,UUID scheduleId,int scheduleRevision,Instant scheduledFor,Instant dueAt,int retryNumber,
            UUID retryOf,TaskState state,UUID leaseToken,Instant leaseUntil,Instant claimedAt,UUID runId,Instant finishedAt,String notes,long skippedSlots) {}
}
