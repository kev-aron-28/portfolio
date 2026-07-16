ALTER TABLE scraping_settings ADD COLUMN indeed_storage_state_path VARCHAR(1024);
ALTER TABLE scraping_settings ADD COLUMN indeed_page_timeout_ms INT NOT NULL DEFAULT 30000;
