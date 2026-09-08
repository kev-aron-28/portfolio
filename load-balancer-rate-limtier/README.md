# Load Balancer and Rate Limiter

Vanilla Java HTTP work: a socket-based server, a round-robin reverse proxy, and standalone rate-limiter strategies.

No Spring, Netty, or external HTTP libraries. The point is to see TCP, HTTP parsing, and concurrency without a framework.

## What is implemented

- HTTP request/response parsing over `ServerSocket` / `Socket`
- A worker pool for concurrent clients
- A load balancer that forwards requests to backends with round-robin
- A demo backend (`TestBackend`)
- Rate-limit strategies as separate classes:
  - Fixed window
  - Sliding window

## What is not wired yet

The rate limiters are not called from the load balancer. There are no health checks, no `429` responses, and no random / least-connections strategies. Token bucket is not implemented.

## Stack

- Java 21
- Maven
- JUnit 5
- Java standard library only (`java.net`, `java.io`, `java.util.concurrent`)

## Run

```bash
mvn test
```

Start backends (for example on 8080 and 8081) with `com.projects.TestBackend`, then `com.projects.App` (load balancer on port 3000).
