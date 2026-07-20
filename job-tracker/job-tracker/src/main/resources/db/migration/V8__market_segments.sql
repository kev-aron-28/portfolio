CREATE TABLE market_segments (
	id BIGSERIAL PRIMARY KEY,
	name VARCHAR(255) NOT NULL UNIQUE,
	description TEXT,
	keywords TEXT,
	filters TEXT,
	created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE job_market_segments (
	job_id BIGINT NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
	segment_id BIGINT NOT NULL REFERENCES market_segments(id) ON DELETE CASCADE,
	PRIMARY KEY (job_id, segment_id)
);

CREATE INDEX idx_job_market_segments_segment_id ON job_market_segments(segment_id);
