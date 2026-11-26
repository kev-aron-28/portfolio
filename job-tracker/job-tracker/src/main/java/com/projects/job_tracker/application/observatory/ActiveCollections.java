package com.projects.job_tracker.application.observatory;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/** Process-local liveness; durable ownership and fencing are stored in the database. */
@Component
public class ActiveCollections {
    private final Map<UUID,Instant> runs=new ConcurrentHashMap<>();
    public void start(UUID id) { runs.put(id,Instant.now()); }
    public void stop(UUID id) { runs.remove(id); }
    public Map<UUID,Instant> snapshot() { return Map.copyOf(runs); }
}
