package com.projects.job_tracker.domain.observatory;

import java.time.Instant;
import java.util.*;

public final class DuplicateModels {
    private DuplicateModels() {}
    public enum Decision { MERGED, DISTINCT, UNREVIEWED }
    public record SourceClaim(UUID postingId, String sourceKey, String url, String externalId, String registrationNote) {}
    public record Snapshot(UUID id, int revision, String title, String company, String source, String url,
            String externalId, String contentHash, String originalText, Instant recordedAt) {}
    public record Review(UUID id, UUID leftId, UUID rightId, int revision, Decision decision, String reason,
            int leftRevision, int rightRevision, Instant recordedAt, String requestHash) {}
    public record ReviewView(Review review, boolean postingDataChanged) {}
    public record Candidate(Snapshot left, Snapshot right, List<String> signals, int expectedRevision) {}
    public record Group(UUID canonicalId, List<Snapshot> members) {}
    public record Identity(SourceClaim sourceIdentity, UUID canonicalId, List<UUID> memberIds) {}
}
