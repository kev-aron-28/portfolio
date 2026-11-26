-- Independent registry: no FK to jobs or applications. Originals are never edited by the application.
CREATE TABLE observatory_postings (
    id UUID PRIMARY KEY,
    source VARCHAR(200) NOT NULL,
    url TEXT,
    original_text TEXT NOT NULL,
    observed_at TIMESTAMP WITH TIME ZONE NOT NULL,
    recorded_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CHECK (observed_at <= recorded_at)
);
CREATE TABLE observatory_posting_versions (
    posting_id UUID NOT NULL REFERENCES observatory_postings(id),
    revision INTEGER NOT NULL CHECK (revision > 0),
    recorded_at TIMESTAMP WITH TIME ZONE NOT NULL,
    reason TEXT NOT NULL,
    method VARCHAR(50) NOT NULL,
    title VARCHAR(500) NOT NULL,
    company TEXT,
    location TEXT,
    requirements TEXT,
    salary_min NUMERIC(14,2) CHECK (salary_min > 0),
    salary_max NUMERIC(14,2) CHECK (salary_max > 0),
    currency VARCHAR(3),
    period VARCHAR(10) CHECK (period IN ('HOUR','DAY','WEEK','MONTH','YEAR')),
    tax_basis VARCHAR(10) CHECK (tax_basis IN ('GROSS','NET')),
    PRIMARY KEY (posting_id, revision),
    CHECK (salary_min IS NULL OR salary_max IS NULL OR salary_min <= salary_max)
);
CREATE INDEX idx_observatory_postings_recorded ON observatory_postings(recorded_at, id);
