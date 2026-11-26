CREATE TABLE observatory_skills (id UUID PRIMARY KEY);
CREATE TABLE observatory_skill_versions (
    skill_id UUID NOT NULL REFERENCES observatory_skills(id),
    revision INTEGER NOT NULL CHECK (revision > 0),
    name VARCHAR(200) NOT NULL,
    reason TEXT NOT NULL,
    recorded_at TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (skill_id, revision)
);
CREATE TABLE observatory_skill_aliases (
    skill_id UUID NOT NULL,
    revision INTEGER NOT NULL,
    alias VARCHAR(200) NOT NULL,
    PRIMARY KEY (skill_id, revision, alias),
    FOREIGN KEY (skill_id, revision) REFERENCES observatory_skill_versions(skill_id, revision)
);
ALTER TABLE observatory_posting_versions ADD COLUMN requirements_reviewed BOOLEAN NOT NULL DEFAULT FALSE;
CREATE TABLE observatory_posting_requirements (
    posting_id UUID NOT NULL,
    posting_revision INTEGER NOT NULL,
    skill_id UUID NOT NULL,
    skill_revision INTEGER NOT NULL,
    obligation VARCHAR(10) NOT NULL CHECK (obligation IN ('REQUIRED', 'DESIRED', 'UNKNOWN')),
    evidence TEXT NOT NULL,
    PRIMARY KEY (posting_id, posting_revision, skill_id),
    FOREIGN KEY (posting_id, posting_revision) REFERENCES observatory_posting_versions(posting_id, revision),
    FOREIGN KEY (skill_id, skill_revision) REFERENCES observatory_skill_versions(skill_id, revision)
);
CREATE INDEX idx_observatory_requirements_skill ON observatory_posting_requirements(skill_id, skill_revision);
-- Starter catalog: editable through new immutable versions.
INSERT INTO observatory_skills(id) VALUES
('10000000-0000-0000-0000-000000000001'),
('10000000-0000-0000-0000-000000000002'),
('10000000-0000-0000-0000-000000000003'),
('10000000-0000-0000-0000-000000000004');
INSERT INTO observatory_skill_versions(skill_id, revision, name, reason, recorded_at) VALUES
('10000000-0000-0000-0000-000000000001', 1, 'Java', 'Catálogo inicial', CURRENT_TIMESTAMP),
('10000000-0000-0000-0000-000000000002', 1, 'SQL', 'Catálogo inicial', CURRENT_TIMESTAMP),
('10000000-0000-0000-0000-000000000003', 1, 'Docker', 'Catálogo inicial', CURRENT_TIMESTAMP),
('10000000-0000-0000-0000-000000000004', 1, 'Spring Boot', 'Catálogo inicial', CURRENT_TIMESTAMP);
INSERT INTO observatory_skill_aliases(skill_id, revision, alias) VALUES
('10000000-0000-0000-0000-000000000004', 1, 'SpringBoot');
