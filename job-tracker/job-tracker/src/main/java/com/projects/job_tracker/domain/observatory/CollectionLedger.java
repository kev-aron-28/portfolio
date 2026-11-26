package com.projects.job_tracker.domain.observatory;

import java.time.Instant;
import java.util.*;

public interface CollectionLedger {
    record Attempt(UUID runId, String status, String searchUrl, String version, Instant startedAt, Instant finishedAt, String notes, Instant heartbeatAt, String terminationKind) {}
    record RawItem(UUID id, UUID runId, String itemKey, Instant observedAt, String listingHtml, String detailHtml,
            boolean detailVerified, String originalText, String parserVersion, String status, String issue,
            UUID postingId, UUID observationId) {}
    Optional<Attempt> attempt(UUID runId);
    void begin(UUID runId, String searchUrl, String version);
    default void finish(UUID runId,String notes) { finish(runId,notes,"NORMAL"); }
    void finish(UUID runId,String notes,String terminationKind);
    Optional<Attempt> lockAttempt(UUID runId);
    void heartbeat(UUID runId,Instant at);
    List<UUID> staleAttempts(Instant heartbeatBefore,Instant startedBefore);
    void page(UUID runId,MarketCollector.PageEvidence page,String version);
    List<MarketCollector.PageEvidence> pages(UUID runId);
    RawItem stage(UUID runId, MarketCollector.Item item, String version);
    void imported(UUID itemId, UUID postingId, UUID observationId);
    void failed(UUID itemId, String issue);
    List<RawItem> items(UUID runId);
}
