CREATE TABLE mock_interviews (
    id                     BIGSERIAL PRIMARY KEY,
    format                 VARCHAR(50) NOT NULL,
    started_at             TIMESTAMPTZ NOT NULL,
    finished_at            TIMESTAMPTZ,
    total_duration_seconds INT NOT NULL DEFAULT 0,
    total_questions        INT NOT NULL DEFAULT 0,
    created_at             TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_mock_interviews_started_at ON mock_interviews (started_at DESC);

CREATE TABLE mock_interview_items (
    id                      BIGSERIAL PRIMARY KEY,
    interview_id            BIGINT NOT NULL REFERENCES mock_interviews (id) ON DELETE CASCADE,
    item_order              INT NOT NULL,
    question_type           VARCHAR(20) NOT NULL,
    problem_id              BIGINT REFERENCES problems (id) ON DELETE SET NULL,
    behavioral_question_id  BIGINT REFERENCES behavioral_questions (id) ON DELETE SET NULL,
    title                   VARCHAR(300) NOT NULL,
    duration_seconds        INT,
    rating                  INT,
    completed_at            TIMESTAMPTZ,
    CONSTRAINT chk_mock_interview_items_type
        CHECK (question_type IN ('ALGORITHM', 'BEHAVIORAL')),
    CONSTRAINT chk_mock_interview_items_rating
        CHECK (rating IS NULL OR rating BETWEEN 1 AND 5),
    CONSTRAINT chk_mock_interview_items_duration
        CHECK (duration_seconds IS NULL OR duration_seconds >= 0),
    CONSTRAINT chk_mock_interview_items_source
        CHECK (
            (question_type = 'ALGORITHM' AND problem_id IS NOT NULL)
            OR (question_type = 'BEHAVIORAL' AND behavioral_question_id IS NOT NULL)
        )
);

CREATE INDEX idx_mock_interview_items_interview_id ON mock_interview_items (interview_id);
CREATE UNIQUE INDEX uq_mock_interview_items_order ON mock_interview_items (interview_id, item_order);
