ALTER TABLE observatory_protocol_versions ADD COLUMN collector_platform VARCHAR(20);
ALTER TABLE observatory_protocol_versions ADD COLUMN collector_location TEXT;
ALTER TABLE observatory_protocol_versions ADD COLUMN collector_work_mode VARCHAR(10);
ALTER TABLE observatory_protocol_versions ADD COLUMN collector_posted_days INTEGER;
CREATE TABLE observatory_collection_attempts (
    run_id UUID PRIMARY KEY REFERENCES observatory_capture_runs(id),
    status VARCHAR(12) NOT NULL CHECK (status IN ('RUNNING','FINISHED')),
    search_url TEXT NOT NULL, collector_version VARCHAR(100) NOT NULL,
    started_at TIMESTAMP WITH TIME ZONE NOT NULL, finished_at TIMESTAMP WITH TIME ZONE, notes TEXT
);
CREATE TABLE observatory_collected_items (
    id UUID PRIMARY KEY, run_id UUID NOT NULL REFERENCES observatory_capture_runs(id), item_key VARCHAR(200) NOT NULL,
    observed_at TIMESTAMP WITH TIME ZONE NOT NULL, recorded_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP, listing_html TEXT, detail_html TEXT,
    detail_verified BOOLEAN NOT NULL, original_text TEXT, parser_version VARCHAR(100) NOT NULL,
    status VARCHAR(10) NOT NULL CHECK (status IN ('PENDING','IMPORTED','ERROR')), issue TEXT,
    posting_id UUID REFERENCES observatory_postings(id), observation_id UUID REFERENCES observatory_observations(id),
    UNIQUE(run_id,item_key)
);
