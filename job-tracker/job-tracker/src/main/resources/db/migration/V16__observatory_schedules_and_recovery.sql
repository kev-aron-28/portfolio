ALTER TABLE observatory_collection_attempts ADD COLUMN heartbeat_at TIMESTAMP WITH TIME ZONE;
UPDATE observatory_collection_attempts SET heartbeat_at = started_at;
ALTER TABLE observatory_collection_attempts ALTER COLUMN heartbeat_at SET NOT NULL;
ALTER TABLE observatory_collection_attempts ADD COLUMN termination_kind VARCHAR(40);
CREATE INDEX idx_observatory_attempt_heartbeat ON observatory_collection_attempts(status,heartbeat_at);
CREATE TABLE observatory_schedule_registry (id INTEGER PRIMARY KEY CHECK (id = 1));
INSERT INTO observatory_schedule_registry(id) VALUES (1);
CREATE TABLE observatory_schedules (
    id UUID PRIMARY KEY, current_revision INTEGER NOT NULL, enabled BOOLEAN NOT NULL,
    request_key UUID NOT NULL UNIQUE, creation_hash VARCHAR(64) NOT NULL,
    next_run_at TIMESTAMP WITH TIME ZONE NOT NULL, active_task_id UUID,
    last_run_id UUID REFERENCES observatory_capture_runs(id), consecutive_failures INTEGER NOT NULL DEFAULT 0
);
CREATE TABLE observatory_schedule_versions (
    schedule_id UUID NOT NULL REFERENCES observatory_schedules(id), revision INTEGER NOT NULL CHECK (revision > 0),
    name VARCHAR(200) NOT NULL, protocol_id UUID NOT NULL, protocol_revision INTEGER NOT NULL,
    enabled BOOLEAN NOT NULL, start_at TIMESTAMP WITH TIME ZONE NOT NULL,
    interval_minutes INTEGER NOT NULL CHECK (interval_minutes > 0), max_retries INTEGER NOT NULL CHECK (max_retries BETWEEN 0 AND 5),
    backoff_minutes INTEGER NOT NULL CHECK (backoff_minutes BETWEEN 1 AND 1440), reason TEXT NOT NULL,
    recorded_at TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY(schedule_id,revision),
    FOREIGN KEY(protocol_id,protocol_revision) REFERENCES observatory_protocol_versions(protocol_id,revision)
);
CREATE TABLE observatory_schedule_tasks (
    id UUID PRIMARY KEY, schedule_id UUID NOT NULL, schedule_revision INTEGER NOT NULL,
    scheduled_for TIMESTAMP WITH TIME ZONE NOT NULL, due_at TIMESTAMP WITH TIME ZONE NOT NULL,
    retry_number INTEGER NOT NULL CHECK (retry_number >= 0), retry_of UUID REFERENCES observatory_schedule_tasks(id),
    state VARCHAR(12) NOT NULL CHECK (state IN ('PENDING','CLAIMED','RUNNING','SUCCEEDED','FAILED','ABANDONED','CANCELLED')),
    lease_token UUID, lease_until TIMESTAMP WITH TIME ZONE, claimed_at TIMESTAMP WITH TIME ZONE,
    run_id UUID UNIQUE REFERENCES observatory_capture_runs(id), finished_at TIMESTAMP WITH TIME ZONE, notes TEXT,
    skipped_slots BIGINT NOT NULL DEFAULT 0 CHECK (skipped_slots >= 0),
    FOREIGN KEY(schedule_id,schedule_revision) REFERENCES observatory_schedule_versions(schedule_id,revision),
    UNIQUE(schedule_id,schedule_revision,scheduled_for,retry_number),
    CHECK (state NOT IN ('CLAIMED','RUNNING') OR (lease_token IS NOT NULL AND lease_until IS NOT NULL AND claimed_at IS NOT NULL))
);
ALTER TABLE observatory_schedules ADD CONSTRAINT fk_observatory_schedule_active FOREIGN KEY(active_task_id) REFERENCES observatory_schedule_tasks(id);
CREATE INDEX idx_observatory_schedule_due ON observatory_schedules(enabled,next_run_at);
CREATE INDEX idx_observatory_task_lease ON observatory_schedule_tasks(state,lease_until);
CREATE INDEX idx_observatory_task_schedule ON observatory_schedule_tasks(schedule_id,due_at);
