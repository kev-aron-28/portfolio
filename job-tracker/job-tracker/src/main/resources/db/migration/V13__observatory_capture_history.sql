CREATE TABLE observatory_segments (id UUID PRIMARY KEY);
CREATE TABLE observatory_segment_versions (
    segment_id UUID NOT NULL REFERENCES observatory_segments(id), revision INTEGER NOT NULL CHECK (revision > 0),
    name VARCHAR(200) NOT NULL, description TEXT, role_family VARCHAR(200) NOT NULL, country_code VARCHAR(2),
    seniority VARCHAR(10) CHECK (seniority IN ('TRAINEE','JUNIOR','MID','SENIOR','LEAD')),
    work_mode VARCHAR(10) CHECK (work_mode IN ('ONSITE','HYBRID','REMOTE')), reason TEXT NOT NULL, recorded_at TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY(segment_id, revision)
);
CREATE TABLE observatory_protocols (id UUID PRIMARY KEY);
CREATE TABLE observatory_protocol_versions (
    protocol_id UUID NOT NULL REFERENCES observatory_protocols(id), revision INTEGER NOT NULL CHECK (revision > 0),
    segment_id UUID NOT NULL, segment_revision INTEGER NOT NULL,
    name VARCHAR(200) NOT NULL, source VARCHAR(200) NOT NULL, query_text TEXT NOT NULL, filters TEXT NOT NULL,
    interval_minutes INTEGER NOT NULL CHECK (interval_minutes > 0 AND interval_minutes <= 525600),
    result_limit INTEGER NOT NULL CHECK (result_limit > 0 AND result_limit <= 10000),
    ordering_rule TEXT NOT NULL, pagination TEXT NOT NULL,
    sampling_mode VARCHAR(25) NOT NULL CHECK (sampling_mode IN ('EXPLORATORY', 'SYSTEMATIC_MANUAL')),
    reason TEXT NOT NULL, recorded_at TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY(protocol_id, revision),
    FOREIGN KEY(segment_id, segment_revision) REFERENCES observatory_segment_versions(segment_id, revision)
);
CREATE TABLE observatory_capture_runs (
    id UUID PRIMARY KEY, protocol_id UUID NOT NULL, protocol_revision INTEGER NOT NULL,
    request_key UUID NOT NULL, started_at TIMESTAMP WITH TIME ZONE NOT NULL,
    finished_at TIMESTAMP WITH TIME ZONE, state VARCHAR(10) NOT NULL CHECK (state IN ('RUNNING','COMPLETE','PARTIAL','FAILED')),
    pages_visited INTEGER CHECK (pages_visited >= 0), truncated BOOLEAN NOT NULL DEFAULT FALSE, notes TEXT,
    FOREIGN KEY(protocol_id, protocol_revision) REFERENCES observatory_protocol_versions(protocol_id, revision),
    UNIQUE(protocol_id, request_key), CHECK (finished_at IS NULL OR finished_at >= started_at),
    CHECK ((state = 'RUNNING' AND finished_at IS NULL) OR (state <> 'RUNNING' AND finished_at IS NOT NULL))
);
CREATE TABLE observatory_observations (
    id UUID PRIMARY KEY, posting_id UUID NOT NULL REFERENCES observatory_postings(id),
    run_id UUID REFERENCES observatory_capture_runs(id), item_key VARCHAR(200), request_fingerprint VARCHAR(64),
    source VARCHAR(200) NOT NULL, url TEXT, original_text TEXT, content_hash VARCHAR(64),
    observed_at TIMESTAMP WITH TIME ZONE NOT NULL, recorded_at TIMESTAMP WITH TIME ZONE NOT NULL,
    availability VARCHAR(12) NOT NULL CHECK (availability IN ('AVAILABLE','UNAVAILABLE','UNKNOWN')),
    data_revision INTEGER,
    UNIQUE(run_id, item_key), UNIQUE(id, posting_id),
    FOREIGN KEY(posting_id, data_revision) REFERENCES observatory_posting_versions(posting_id, revision),
    CHECK (observed_at <= recorded_at),
    CHECK ((run_id IS NULL AND item_key IS NULL) OR (run_id IS NOT NULL AND item_key IS NOT NULL AND request_fingerprint IS NOT NULL)),
    CHECK (availability <> 'AVAILABLE' OR original_text IS NOT NULL)
);
-- Preserve original manual evidence; no synthetic protocol, capture or historical timestamps.
INSERT INTO observatory_observations(id, posting_id, source, url, original_text, observed_at, recorded_at, availability, data_revision)
SELECT id, id, source, url, original_text, observed_at, recorded_at, 'AVAILABLE', 1 FROM observatory_postings;
ALTER TABLE observatory_posting_versions ADD COLUMN observation_id UUID;
UPDATE observatory_posting_versions SET observation_id = posting_id;
ALTER TABLE observatory_posting_versions ALTER COLUMN observation_id SET NOT NULL;
ALTER TABLE observatory_posting_versions ADD CONSTRAINT fk_observatory_version_evidence
    FOREIGN KEY(observation_id, posting_id) REFERENCES observatory_observations(id, posting_id);
CREATE INDEX idx_observatory_observations_posting ON observatory_observations(posting_id, observed_at);
CREATE INDEX idx_observatory_runs_started ON observatory_capture_runs(started_at, id);
