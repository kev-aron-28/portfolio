package com.projects.job_tracker.application.observatory;

import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ObservatoryScheduleWorker {
    private final ObservatoryScheduleService schedules;
    private final RunCollectionService collection;
    public ObservatoryScheduleWorker(ObservatoryScheduleService schedules,RunCollectionService collection) { this.schedules=schedules; this.collection=collection; }
    public void execute(UUID id,UUID token) {
        var bound=schedules.bind(id,token,Instant.now());
        if (bound.isEmpty()) return;
        try { collection.collect(bound.get().runId()); }
        finally { schedules.complete(id,token,Instant.now()); }
    }
}
