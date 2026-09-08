# URL Shortener

A Spring Boot service that turns a long URL into an 8-character code and redirects that code back to the original address.

Mappings are stored in PostgreSQL. Redirect lookups go through Redis (24-hour TTL) before hitting the database.

## API

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/api/v1/shorten` | Hash the long URL and persist the mapping |
| `GET` | `/api/v1/{shortUrl}` | 302 redirect to the long URL |

Short codes are SHA-256 truncated to 8 bytes, then encoded in base62. On collision, the hasher retries with a counter appended to the input.

This is a single `api-service`, not a set of microservices. Kubernetes manifests and Docker Compose files are included for local/cluster runs; they do not add extra application services.

## Stack

- Java 21
- Spring Boot
- PostgreSQL
- Redis
- Docker Compose

## Run

```bash
docker compose up
```

Or run `api-service` with local Postgres and Redis:

```bash
cd api-service
./mvnw spring-boot:run
```
