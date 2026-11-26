package com.projects.job_tracker.domain.observatory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MarketPostingRepository {
    void insert(MarketPosting posting);
    void insertCaptured(MarketPosting posting, CaptureModels.Observation initial, String fingerprint);
    Optional<MarketPosting> find(UUID id);
    List<MarketPosting> list(int limit, int offset);
    void lock(UUID id);
    void append(UUID id, int expectedRevision, MarketPosting.Revision revision);
}
