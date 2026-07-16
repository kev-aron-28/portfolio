CREATE TABLE scraping_settings (
    id INT PRIMARY KEY,
    rate_limit_ms INT NOT NULL DEFAULT 1000,
    default_max_results INT NOT NULL DEFAULT 20,
    default_platforms VARCHAR(255) NOT NULL DEFAULT 'occ,linkedin',
    linkedin_storage_state_path VARCHAR(1024),
    linkedin_page_timeout_ms INT NOT NULL DEFAULT 30000,
    scheduling_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    scheduling_poll_interval_ms BIGINT NOT NULL DEFAULT 60000
);

INSERT INTO scraping_settings (id) VALUES (1);
