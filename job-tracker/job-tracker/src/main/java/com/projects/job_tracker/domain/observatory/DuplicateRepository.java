package com.projects.job_tracker.domain.observatory;

import java.time.Instant;
import java.util.*;
import com.projects.job_tracker.domain.observatory.DuplicateModels.*;

public interface DuplicateRepository {
    void lockRegistry();
    List<SourceClaim> claimsBySource(String sourceKey);
    Optional<SourceClaim> claim(UUID postingId);
    void attach(SourceClaim claim);
    List<Snapshot> snapshots();
    List<Review> currentReviews(Instant cutoff);
    List<Review> history(UUID reviewId);
    void save(Review review, boolean newPair);
}
