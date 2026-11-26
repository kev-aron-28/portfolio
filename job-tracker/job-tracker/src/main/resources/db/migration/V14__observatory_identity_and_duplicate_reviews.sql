-- Registry row serializes source claims and graph decisions, preserving legacy conflicts for review.
CREATE TABLE observatory_identity_registry (id INTEGER PRIMARY KEY CHECK (id = 1));
INSERT INTO observatory_identity_registry(id) VALUES (1);
CREATE TABLE observatory_source_postings (
    posting_id UUID PRIMARY KEY REFERENCES observatory_postings(id),
    source_key VARCHAR(200) NOT NULL, url TEXT, external_id VARCHAR(300), registration_note TEXT
);
CREATE INDEX idx_observatory_source_key ON observatory_source_postings(source_key);
CREATE INDEX idx_observatory_external_identity ON observatory_source_postings(source_key, external_id);
-- No unique URL constraint: historical conflicts must remain intact and become review candidates.
INSERT INTO observatory_source_postings(posting_id, source_key, url)
SELECT id, LOWER(TRIM(source)), url FROM observatory_postings;
CREATE TABLE observatory_duplicate_pairs (
    id UUID PRIMARY KEY,
    left_id UUID NOT NULL REFERENCES observatory_postings(id),
    right_id UUID NOT NULL REFERENCES observatory_postings(id),
    UNIQUE(left_id, right_id), UNIQUE(id, left_id, right_id), CHECK (left_id <> right_id)
);
CREATE TABLE observatory_duplicate_reviews (
    pair_id UUID NOT NULL REFERENCES observatory_duplicate_pairs(id),
    revision INTEGER NOT NULL CHECK (revision > 0),
    decision VARCHAR(12) NOT NULL CHECK (decision IN ('MERGED','DISTINCT','UNREVIEWED')),
    reason TEXT NOT NULL,
    left_id UUID NOT NULL, left_revision INTEGER NOT NULL,
    right_id UUID NOT NULL, right_revision INTEGER NOT NULL,
    recorded_at TIMESTAMP WITH TIME ZONE NOT NULL,
    request_hash VARCHAR(64) NOT NULL,
    PRIMARY KEY(pair_id, revision),
    FOREIGN KEY(pair_id, left_id, right_id) REFERENCES observatory_duplicate_pairs(id, left_id, right_id),
    FOREIGN KEY(left_id, left_revision) REFERENCES observatory_posting_versions(posting_id, revision),
    FOREIGN KEY(right_id, right_revision) REFERENCES observatory_posting_versions(posting_id, revision)
);
CREATE INDEX idx_observatory_duplicate_review_time ON observatory_duplicate_reviews(recorded_at);
