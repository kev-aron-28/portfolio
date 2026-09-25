ALTER TABLE applications
    ADD COLUMN status_changed_at TIMESTAMP WITH TIME ZONE;

UPDATE applications
SET status_changed_at = COALESCE(applied_at, NOW())
WHERE status_changed_at IS NULL;

ALTER TABLE applications
    ALTER COLUMN status_changed_at SET NOT NULL;
