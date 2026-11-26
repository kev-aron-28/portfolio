package com.projects.job_tracker.domain.observatory;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MarketPosting(UUID id, String source, String url, String originalText,
        Instant observedAt, Instant recordedAt, List<Revision> revisions, List<CaptureModels.Observation> observations) {
    public MarketPosting(UUID id, String source, String url, String originalText, Instant observedAt, Instant recordedAt, List<Revision> revisions) {
        this(id, source, url, originalText, observedAt, recordedAt, revisions, List.of());
    }
    public MarketPosting { revisions = List.copyOf(revisions); observations = List.copyOf(observations); }
    public Revision current() { return revisions.getLast(); }
    public record Revision(int number, Instant recordedAt, String reason, String method, PostingData data, UUID observationId) {
        public Revision(int number, Instant recordedAt, String reason, String method, PostingData data) {
            this(number, recordedAt, reason, method, data, null);
        }
    }
    public Instant getFirstSeenAt() {
        return observations.stream().filter(o -> o.availability() == CaptureModels.Availability.AVAILABLE)
            .map(CaptureModels.Observation::observedAt).min(Instant::compareTo).orElse(observedAt);
    }
    public Instant getLastSeenAt() {
        return observations.stream().filter(o -> o.availability() == CaptureModels.Availability.AVAILABLE)
            .map(CaptureModels.Observation::observedAt).max(Instant::compareTo).orElse(observedAt);
    }
    public CaptureModels.Observation evidenceForCurrentRevision() {
        return observations.stream().filter(o -> o.id().equals(current().observationId())).findFirst().orElseThrow();
    }
}
