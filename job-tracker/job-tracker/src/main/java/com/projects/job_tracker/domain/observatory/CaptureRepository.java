package com.projects.job_tracker.domain.observatory;

import java.util.*;
import com.projects.job_tracker.domain.observatory.CaptureModels.*;

public interface CaptureRepository {
    List<SegmentVersion> segments();
    List<SegmentVersion> segmentHistory(UUID id);
    void saveSegment(SegmentVersion version, Integer expectedRevision);
    List<ProtocolVersion> protocols();
    List<ProtocolVersion> protocolHistory(UUID id);
    void saveProtocol(ProtocolVersion version, Integer expectedRevision);
    void lockProtocol(UUID id);
    Optional<CaptureRun> findRunByKey(UUID protocolId, UUID key);
    void insertRun(CaptureRun run);
    CaptureRun lockRun(UUID id);
    Optional<CaptureRun> findRun(UUID id);
    List<CaptureRun> runs(int limit, int offset);
    void finish(CaptureRun run);
    Optional<Observation> findItem(UUID runId, String itemKey, String fingerprint);
    void insertObservation(Observation observation, String fingerprint);
    void linkRevision(UUID observationId, int revision);
    List<Observation> runObservations(UUID runId);
}
