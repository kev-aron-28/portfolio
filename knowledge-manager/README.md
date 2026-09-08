# Knowledge Manager

A personal study tracker for interview preparation. It stores problems, notes, and solutions, and schedules reviews with spaced repetition.

It is not an online judge: it does not compile or run code.

## What it does

Server-rendered Spring Boot app (Thymeleaf) with PostgreSQL:

- Topics, tags, and algorithm problems (including bulk import)
- Personal solutions and notes
- Review sessions using SM-2 (default) or a fixed-interval scheduler
- Dashboard of items due for review
- Statistics
- Behavioral questions and practice
- System-design notes, practice, and a simple whiteboard save
- Mock interviews driven by interview profiles
- Vision boards

## Stack

- Java 21
- Spring Boot
- Thymeleaf
- PostgreSQL
- Flyway
- Docker Compose

## Run

```bash
docker compose up -d
./mvnw spring-boot:run
```
