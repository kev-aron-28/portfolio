CREATE TABLE reviews (
    id              BIGSERIAL PRIMARY KEY,
    problem_id      BIGINT NOT NULL REFERENCES problems (id) ON DELETE CASCADE,
    review_date     DATE NOT NULL,
    rating          INT NOT NULL,
    notes           TEXT,
    next_review_date DATE NOT NULL,
    review_duration INT NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_reviews_rating CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT chk_reviews_duration CHECK (review_duration >= 0)
);

CREATE INDEX idx_reviews_problem_id ON reviews (problem_id);
CREATE INDEX idx_reviews_review_date ON reviews (review_date);
CREATE INDEX idx_reviews_next_review_date ON reviews (next_review_date);
