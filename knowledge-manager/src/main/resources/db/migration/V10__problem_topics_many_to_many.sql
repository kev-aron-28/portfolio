-- Allow problems to belong to multiple topics (Many-to-Many).

CREATE TABLE problem_topics (
    problem_id BIGINT NOT NULL REFERENCES problems (id) ON DELETE CASCADE,
    topic_id   BIGINT NOT NULL REFERENCES topics (id) ON DELETE CASCADE,
    PRIMARY KEY (problem_id, topic_id)
);

INSERT INTO problem_topics (problem_id, topic_id)
SELECT id, topic_id FROM problems WHERE topic_id IS NOT NULL;

CREATE INDEX idx_problem_topics_topic_id ON problem_topics (topic_id);

ALTER TABLE problems DROP CONSTRAINT IF EXISTS problems_topic_id_fkey;
DROP INDEX IF EXISTS idx_problems_topic_id;
ALTER TABLE problems DROP COLUMN topic_id;
