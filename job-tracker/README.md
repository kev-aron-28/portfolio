# Job Tracker

A personal ATS: scrape job posts, keep them in one database, track applications, and look at simple market stats.

It is a Spring Boot app with a Thymeleaf UI and a JSON API. Scrapers use Playwright and Jsoup against LinkedIn, Indeed, OCC, and Computrabajo. Those sites need a saved browser session; they are not a public, unattended API.

## What it does

- Store jobs, companies, and applications (PostgreSQL + Flyway)
- Search profiles and market segments
- Run scrapes on demand or on a schedule
- Dashboard metrics and basic text analytics
- Optional RabbitMQ notifications when jobs are imported

Web routes include `/`, `/jobs`, `/scraping`, and `/segments`. The REST surface covers jobs, applications, profiles, scrape runs, and schedules under `/api/`.

## Stack

- Java 21
- Spring Boot
- Thymeleaf
- PostgreSQL
- Flyway
- Playwright / Jsoup
- RabbitMQ
- Docker Compose

## Run

```bash
docker compose up -d
cd job-tracker
./mvnw spring-boot:run
```
