# Scraping Strategy

The system collects job postings from multiple platforms.

## Architecture

Fetcher → Raw Data → Parser → Domain Model → Storage

## Rules

- Each platform must implement a JobScraper
- Fetching and parsing must be separated
- Prefer JSON endpoints when available
- Use Playwright for dynamic pages
- Use Jsoup only for HTML parsing

## Platforms

### LinkedIn
- Requires browser automation
- Uses Playwright
- Requires session handling

### OCC
- Prefer direct HTTP requests if possible
- Parse HTML or JSON responses

## Constraints

- Rate limiting must be respected
- Duplicates must be detected and removed
- Scrapers must be independent