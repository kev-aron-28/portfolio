CREATE TABLE interview_profiles (
    id                          BIGSERIAL PRIMARY KEY,
    name                        VARCHAR(100) NOT NULL,
    description                 VARCHAR(1000),
    company                     VARCHAR(100),
    color                       VARCHAR(7) NOT NULL DEFAULT '#2563eb',
    icon                        VARCHAR(50) NOT NULL DEFAULT 'bi-briefcase',
    behavioral_question_count   INT NOT NULL DEFAULT 0,
    algorithm_question_count    INT NOT NULL DEFAULT 0,
    system_design_question_count INT NOT NULL DEFAULT 0,
    max_duration_minutes        INT,
    archived                    BOOLEAN NOT NULL DEFAULT FALSE,
    extra_settings_json         TEXT,
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_interview_profiles_name UNIQUE (name),
    CONSTRAINT chk_interview_profiles_behavioral_count
        CHECK (behavioral_question_count >= 0),
    CONSTRAINT chk_interview_profiles_algorithm_count
        CHECK (algorithm_question_count >= 0),
    CONSTRAINT chk_interview_profiles_system_design_count
        CHECK (system_design_question_count >= 0),
    CONSTRAINT chk_interview_profiles_max_duration
        CHECK (max_duration_minutes IS NULL OR max_duration_minutes > 0),
    CONSTRAINT chk_interview_profiles_has_questions
        CHECK (
            behavioral_question_count
            + algorithm_question_count
            + system_design_question_count > 0
        )
);

CREATE INDEX idx_interview_profiles_archived ON interview_profiles (archived);
CREATE INDEX idx_interview_profiles_company ON interview_profiles (company);

CREATE TABLE interview_profile_target_difficulties (
    profile_id BIGINT NOT NULL REFERENCES interview_profiles (id) ON DELETE CASCADE,
    difficulty VARCHAR(20) NOT NULL,
    PRIMARY KEY (profile_id, difficulty),
    CONSTRAINT chk_interview_profile_target_difficulty
        CHECK (difficulty IN ('EASY', 'MEDIUM', 'HARD'))
);

CREATE TABLE interview_profile_problems (
    profile_id BIGINT NOT NULL REFERENCES interview_profiles (id) ON DELETE CASCADE,
    problem_id BIGINT NOT NULL REFERENCES problems (id) ON DELETE CASCADE,
    PRIMARY KEY (profile_id, problem_id)
);

CREATE INDEX idx_interview_profile_problems_problem_id
    ON interview_profile_problems (problem_id);

CREATE TABLE interview_profile_behavioral_questions (
    profile_id             BIGINT NOT NULL REFERENCES interview_profiles (id) ON DELETE CASCADE,
    behavioral_question_id BIGINT NOT NULL REFERENCES behavioral_questions (id) ON DELETE CASCADE,
    PRIMARY KEY (profile_id, behavioral_question_id)
);

CREATE INDEX idx_interview_profile_behavioral_question_id
    ON interview_profile_behavioral_questions (behavioral_question_id);

CREATE TABLE interview_profile_system_design_problems (
    profile_id               BIGINT NOT NULL REFERENCES interview_profiles (id) ON DELETE CASCADE,
    system_design_problem_id BIGINT NOT NULL REFERENCES system_design_problems (id) ON DELETE CASCADE,
    PRIMARY KEY (profile_id, system_design_problem_id)
);

CREATE INDEX idx_interview_profile_system_design_problem_id
    ON interview_profile_system_design_problems (system_design_problem_id);

ALTER TABLE mock_interviews
    ADD COLUMN profile_id BIGINT REFERENCES interview_profiles (id) ON DELETE SET NULL;

CREATE INDEX idx_mock_interviews_profile_id ON mock_interviews (profile_id);
