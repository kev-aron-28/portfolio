ALTER TABLE jobs ADD COLUMN external_id VARCHAR(100);
ALTER TABLE jobs ADD COLUMN posted_at TIMESTAMP WITH TIME ZONE;
ALTER TABLE jobs ADD COLUMN employment_type VARCHAR(100);
ALTER TABLE jobs ADD COLUMN work_mode VARCHAR(50);
ALTER TABLE jobs ADD COLUMN category VARCHAR(255);
ALTER TABLE jobs ADD COLUMN subcategory VARCHAR(255);
ALTER TABLE jobs ADD COLUMN benefits TEXT;
ALTER TABLE jobs ADD COLUMN requirements TEXT;

CREATE INDEX idx_jobs_posted_at ON jobs (posted_at DESC);
CREATE INDEX idx_jobs_work_mode ON jobs (work_mode);
CREATE INDEX idx_jobs_employment_type ON jobs (employment_type);
CREATE INDEX idx_jobs_category ON jobs (category);
