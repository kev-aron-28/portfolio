# Feature Flag System

A centralized feature-flag service. Other applications ask whether a capability is on for a given user, instead of shipping a new deploy every time a rollout changes.

Typical uses:

- Progressive releases
- Role- or user-specific activation
- Percentage rollouts
- Turning a feature off without a rollback

The evaluation contract the rest of a system would use:

```java
boolean enabled = featureFlag.evaluate(context);
```

## Evaluation

A flag is decided in the domain model, not in the controller.

```
enabled == false  →  always off

enabled == true
  and no rules    →  on for everyone
  and any rule    →  on if one rule matches
```

Rules (OR):

| Rule | Meaning |
| --- | --- |
| Role | The user has at least one allowed role |
| User | The user id is in the allow-list |
| Percentage | `abs(user.hashCode()) % 100 < rolloutPercentage` |

If `enabled` is false, role, user, and percentage rules are ignored.

Keys cannot be blank. Rollout, when set, must be between 0 and 100.

## Architecture

The service follows a simple hexagonal split:

```
domain/         FeatureFlag, Role, User, evaluation, invariants
application/    use cases (register, login, save flag)
infra/          HTTP, JWT, PostgreSQL, Redis config
```

PostgreSQL is the source of truth. Redis is provisioned for a future evaluation cache; flag reads are not cached yet.

## Current API

Auth is JWT. `/api/auth/**` is public. Everything else requires a Bearer token.

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/api/auth/signup` | Register with email and password |
| `POST` | `/api/auth/login` | Returns a JWT |
| `POST` | `/api/features` | Create a feature flag |

Create body:

```json
{
  "key": "new-checkout",
  "description": "New checkout flow",
  "rolloutPercentage": 10,
  "roles": ["ADMIN", "MANAGER"],
  "users": ["11111111-1111-1111-1111-111111111111"]
}
```

`roles` and `users` can be empty. A flag created through this endpoint starts enabled.

Not exposed yet: list, get, update, delete, and an HTTP evaluate endpoint. Those belong on the service, but evaluation already lives on `FeatureFlag`.

## Run locally

Java 17, Docker, and Maven (wrapper included).

```bash
docker compose up -d
psql postgresql://feature_user:feature_pass@localhost:5432/feature_flags_db \
  -f backend/src/main/java/com/portfolio/backend/infra/data/model.sql
cd backend
./mvnw spring-boot:run
```

The API listens on `http://localhost:8080`.

## Stack

- Java 17
- Spring Boot
- PostgreSQL
- Redis
- JWT
- Docker Compose
