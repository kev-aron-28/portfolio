ALTER TABLE observatory_protocol_versions ADD COLUMN collector_max_pages INTEGER DEFAULT 1;
ALTER TABLE observatory_protocol_versions ADD CONSTRAINT observatory_max_pages CHECK (collector_max_pages BETWEEN 1 AND 20);
CREATE TABLE observatory_collection_pages (
    run_id UUID NOT NULL REFERENCES observatory_capture_runs(id),
    page_number INTEGER NOT NULL CHECK (page_number > 0),
    url TEXT NOT NULL, observed_at TIMESTAMP WITH TIME ZONE NOT NULL,
    recorded_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    html TEXT NOT NULL, card_count INTEGER NOT NULL CHECK (card_count >= 0),
    fingerprint VARCHAR(64) NOT NULL, next_url TEXT, outcome VARCHAR(40) NOT NULL,
    collector_version VARCHAR(100) NOT NULL,
    PRIMARY KEY (run_id,page_number)
);
