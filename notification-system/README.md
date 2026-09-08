# Notification System

An API plus a worker that queues notification jobs, renders templates, and attempts delivery.

The worker currently processes the **email** queue. SMS routing keys exist in RabbitMQ config, but there is no SMS listener. Delivery uses a fake provider (random failures) so retries can be exercised locally. There is no rate limiter and no real email/SMS gateway.

## What it does

**API** (`api-service`)

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/api/v1/templates` | Create a template with `{{placeholders}}` |
| `POST` | `/api/v1/send` | Validate payload, persist the notification, publish to the queue |

**Worker** (`message-worker`)

- Consumes `email.queue`
- Loads the template (Redis cache on the template lookup)
- Renders placeholders
- Calls `FakeNotificationProvider`
- Retries up to 3 times through RabbitMQ

PostgreSQL holds templates and notifications. Apply `api-service/src/main/resources/schema.sql` yourself; it is not migrated automatically.

## Stack

- Java 21
- Spring Boot
- PostgreSQL
- RabbitMQ
- Redis
- Docker Compose

## Run

```bash
docker compose up -d
# apply schema.sql to Postgres, then:
cd api-service && ./mvnw spring-boot:run
cd message-worker && ./mvnw spring-boot:run
```
