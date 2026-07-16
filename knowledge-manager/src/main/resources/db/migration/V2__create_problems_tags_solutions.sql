CREATE TABLE tags (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_tags_name UNIQUE (name)
);

CREATE TABLE problems (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(200) NOT NULL,
    url         VARCHAR(500),
    difficulty  VARCHAR(20) NOT NULL,
    description TEXT,
    favorite    BOOLEAN NOT NULL DEFAULT FALSE,
    archived    BOOLEAN NOT NULL DEFAULT FALSE,
    topic_id    BIGINT NOT NULL REFERENCES topics (id),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE solutions (
    id          BIGSERIAL PRIMARY KEY,
    problem_id  BIGINT NOT NULL UNIQUE REFERENCES problems (id) ON DELETE CASCADE,
    language    VARCHAR(50) NOT NULL DEFAULT 'java',
    source_code TEXT,
    explanation TEXT,
    complexity  TEXT,
    mistakes    TEXT
);

CREATE TABLE problem_tags (
    problem_id BIGINT NOT NULL REFERENCES problems (id) ON DELETE CASCADE,
    tag_id     BIGINT NOT NULL REFERENCES tags (id) ON DELETE CASCADE,
    PRIMARY KEY (problem_id, tag_id)
);

CREATE INDEX idx_problems_topic_id ON problems (topic_id);
CREATE INDEX idx_problems_archived ON problems (archived);
