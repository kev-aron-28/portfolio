CREATE TABLE behavioral_questions (
    id               BIGSERIAL PRIMARY KEY,
    title            VARCHAR(200) NOT NULL,
    category         VARCHAR(50) NOT NULL,
    question         TEXT NOT NULL,
    answer_situation TEXT,
    answer_task      TEXT,
    answer_action    TEXT,
    answer_result    TEXT,
    notes            TEXT,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_behavioral_questions_category ON behavioral_questions (category);
CREATE INDEX idx_behavioral_questions_title ON behavioral_questions (title);

CREATE TABLE behavioral_practices (
    id               BIGSERIAL PRIMARY KEY,
    question_id      BIGINT NOT NULL REFERENCES behavioral_questions (id) ON DELETE CASCADE,
    practice_date    DATE NOT NULL,
    duration_seconds INT NOT NULL DEFAULT 0,
    rating           INT NOT NULL,
    next_review_date DATE NOT NULL,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_behavioral_practices_rating CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT chk_behavioral_practices_duration CHECK (duration_seconds >= 0)
);

CREATE INDEX idx_behavioral_practices_question_id ON behavioral_practices (question_id);
CREATE INDEX idx_behavioral_practices_next_review_date ON behavioral_practices (next_review_date);
CREATE INDEX idx_behavioral_practices_practice_date ON behavioral_practices (practice_date);
