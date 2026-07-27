CREATE TABLE system_design_problems (
    id                         BIGSERIAL PRIMARY KEY,
    title                      VARCHAR(200) NOT NULL,
    category                   VARCHAR(50) NOT NULL,
    difficulty                 VARCHAR(20) NOT NULL,
    description                TEXT NOT NULL,
    original_source            VARCHAR(500),
    estimated_interview_time   INT,
    favorite                   BOOLEAN NOT NULL DEFAULT FALSE,
    whiteboard_json            TEXT,
    created_at                 TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at                 TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_system_design_difficulty
        CHECK (difficulty IN ('EASY', 'MEDIUM', 'HARD')),
    CONSTRAINT chk_system_design_estimated_time
        CHECK (estimated_interview_time IS NULL OR estimated_interview_time > 0)
);

CREATE INDEX idx_system_design_problems_category ON system_design_problems (category);
CREATE INDEX idx_system_design_problems_difficulty ON system_design_problems (difficulty);
CREATE INDEX idx_system_design_problems_title ON system_design_problems (title);
CREATE INDEX idx_system_design_problems_favorite ON system_design_problems (favorite);

CREATE TABLE system_design_documents (
    id                          BIGSERIAL PRIMARY KEY,
    problem_id                  BIGINT NOT NULL UNIQUE
                                REFERENCES system_design_problems (id) ON DELETE CASCADE,
    overview                    TEXT,
    functional_requirements     TEXT,
    non_functional_requirements TEXT,
    assumptions                 TEXT,
    high_level_architecture     TEXT,
    components                  TEXT,
    database_design             TEXT,
    api_design                  TEXT,
    scaling_strategy            TEXT,
    caching_strategy            TEXT,
    load_balancing              TEXT,
    messaging                   TEXT,
    tradeoffs                   TEXT,
    bottlenecks                 TEXT,
    lessons_learned             TEXT,
    personal_notes              TEXT
);

CREATE TABLE system_design_tags (
    problem_id BIGINT NOT NULL REFERENCES system_design_problems (id) ON DELETE CASCADE,
    tag        VARCHAR(100) NOT NULL,
    PRIMARY KEY (problem_id, tag)
);

CREATE INDEX idx_system_design_tags_tag ON system_design_tags (tag);

CREATE TABLE system_design_reviews (
    id               BIGSERIAL PRIMARY KEY,
    problem_id       BIGINT NOT NULL REFERENCES system_design_problems (id) ON DELETE CASCADE,
    review_date      DATE NOT NULL,
    duration_seconds INT NOT NULL DEFAULT 0,
    rating           INT NOT NULL,
    next_review_date DATE NOT NULL,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_system_design_reviews_rating CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT chk_system_design_reviews_duration CHECK (duration_seconds >= 0)
);

CREATE INDEX idx_system_design_reviews_problem_id ON system_design_reviews (problem_id);
CREATE INDEX idx_system_design_reviews_next_review_date ON system_design_reviews (next_review_date);
CREATE INDEX idx_system_design_reviews_review_date ON system_design_reviews (review_date);

ALTER TABLE mock_interview_items
    ADD COLUMN system_design_problem_id BIGINT
        REFERENCES system_design_problems (id) ON DELETE SET NULL;

ALTER TABLE mock_interview_items
    DROP CONSTRAINT chk_mock_interview_items_type;

ALTER TABLE mock_interview_items
    ADD CONSTRAINT chk_mock_interview_items_type
        CHECK (question_type IN ('ALGORITHM', 'BEHAVIORAL', 'SYSTEM_DESIGN'));

ALTER TABLE mock_interview_items
    DROP CONSTRAINT chk_mock_interview_items_source;

ALTER TABLE mock_interview_items
    ADD CONSTRAINT chk_mock_interview_items_source
        CHECK (
            (question_type = 'ALGORITHM' AND problem_id IS NOT NULL)
            OR (question_type = 'BEHAVIORAL' AND behavioral_question_id IS NOT NULL)
            OR (question_type = 'SYSTEM_DESIGN' AND system_design_problem_id IS NOT NULL)
        );
