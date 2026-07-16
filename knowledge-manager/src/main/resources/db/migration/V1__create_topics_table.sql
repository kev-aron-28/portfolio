CREATE TABLE topics (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    color       VARCHAR(7) NOT NULL DEFAULT '#6366f1',
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_topics_name UNIQUE (name)
);
