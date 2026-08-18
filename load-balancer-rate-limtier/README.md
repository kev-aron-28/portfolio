# Vanilla Java Load Balancer

A simple HTTP Load Balancer implemented from scratch using **Vanilla Java**.

The project focuses on practicing:

* HTTP
* TCP networking
* Java sockets
* Concurrency
* Load balancing
* Rate limiting
* Connection management
* Health checks
* Thread safety
* Performance

The implementation should use only the **Java Standard Library** for the core functionality.

No Spring, Netty, Undertow, Apache HTTP Server, Bucket4j, Redis, or external load-balancing/rate-limiting libraries.

The main goal is not to build a production-ready load balancer, but to understand how the underlying pieces work.

---

# Architecture

The final architecture will look approximately like this:

```text
                         HTTP Clients
                              |
                              | HTTP
                              v
                   +----------------------+
                   |    Load Balancer     |
                   |                      |
                   |    HTTP Server       |
                   |    Rate Limiter      |
                   |    LB Strategy       |
                   |    Health Checker     |
                   +----------+-----------+
                              |
                  +-----------+-----------+
                  |           |           |
                HTTP        HTTP        HTTP
                  |           |           |
                  v           v           v
             +---------+ +---------+ +---------+
             |Backend 1| |Backend 2| |Backend 3|
             +---------+ +---------+ +---------+
```

The client communicates with the load balancer.

The load balancer:

1. Receives the HTTP request.
2. Parses the request.
3. Applies the rate limiter.
4. Selects a healthy backend.
5. Opens a connection to the backend.
6. Forwards the HTTP request.
7. Receives the backend response.
8. Returns the response to the client.

---

# Main Goal

Build a small HTTP load balancer capable of:

```text
Client
  |
  | HTTP Request
  v
Load Balancer
  |
  +--> HTTP Parser
  |
  +--> Rate Limiter
  |
  +--> Backend Selection
  |
  +--> Backend Connection
  |
  +--> HTTP Request Forwarding
  |
  v
HTTP Response
  |
  v
Client
```

The project will be developed incrementally.

The first version will use simple blocking I/O. More advanced networking techniques such as NIO will be considered only after the basic implementation is understood.

---

# Technologies

* Java 17+
* Maven
* Java Standard Library

Main packages/classes:

```text
java.net
java.io
java.nio
java.util
java.util.concurrent
java.time
```

The project should avoid high-level frameworks that hide the networking implementation.

In particular, the initial HTTP server should **not** use:

```text
com.sun.net.httpserver.HttpServer
Spring
Netty
Undertow
```

The HTTP server should be implemented using:

```text
ServerSocket
Socket
InputStream
OutputStream
```

---

# TODO

## Phase 1 — Minimal HTTP Server

Goal: create a basic HTTP server directly on top of Java sockets.

### Networking

* [ ] Create a `ServerSocket`.
* [ ] Configure the server port.
* [ ] Start listening for connections.
* [ ] Accept a client using `Socket`.
* [ ] Read bytes from the socket.
* [ ] Write bytes to the socket.
* [ ] Close the connection correctly.
* [ ] Handle socket exceptions.

### HTTP

* [ ] Understand the structure of an HTTP request.
* [ ] Read the request line.
* [ ] Parse the HTTP method.
* [ ] Parse the request path.
* [ ] Parse the HTTP version.
* [ ] Parse HTTP headers.
* [ ] Detect the end of the headers.
* [ ] Create an `HttpRequest` representation.
* [ ] Create an `HttpResponse` representation.
* [ ] Serialize an HTTP response.
* [ ] Return `200 OK`.
* [ ] Return `400 Bad Request`.
* [ ] Return `404 Not Found`.

Example request:

```text
GET /hello HTTP/1.1
Host: localhost:8080
User-Agent: curl
Accept: */*
```

Example response:

```text
HTTP/1.1 200 OK
Content-Type: text/plain
Content-Length: 5

Hello
```

### Testing

* [ ] Test with a browser.
* [ ] Test with `curl`.
* [ ] Test different HTTP paths.
* [ ] Test unknown paths.
* [ ] Test malformed requests.

First milestone:

```text
curl http://localhost:8080/hello
```

should reach the Java server and receive a response generated entirely by the application.

---

# Phase 2 — HTTP Request/Response Model

Separate the networking layer from the HTTP representation.

### Request

Create something conceptually similar to:

```text
HttpRequest

- method
- path
- version
- headers
- body
```

### Response

Create:

```text
HttpResponse

- statusCode
- reasonPhrase
- headers
- body
```

TODO:

* [ ] Create `HttpParser`.
* [ ] Handle unsupported methods.
* [ ] Create `HttpRequest`.
* [ ] Create `HttpResponse`.
* [ ] Create HTTP serialization logic.
* [ ] Separate parsing from socket handling.
* [ ] Support multiple HTTP methods.
* [ ] Support request bodies.
* [ ] Correctly calculate `Content-Length`.
* [ ] Handle empty bodies.

---

# Phase 3 — Concurrent HTTP Server

Goal: support multiple clients simultaneously.

Initial implementation:

```text
Client
  |
  v
ServerSocket
  |
  +--> Thread
  |
  +--> Thread
  |
  +--> Thread
```

TODO:

* [ ] Create one worker per connection.
* [ ] Test simultaneous clients.
* [ ] Replace raw threads with `ExecutorService`.
* [ ] Configure a fixed thread pool.
* [ ] Understand thread pool saturation.
* [ ] Handle client disconnects.
* [ ] Handle socket timeouts.
* [ ] Handle exceptions inside workers.
* [ ] Implement graceful shutdown.

Target:

```text
Client A ----\
Client B -----\
Client C ------> HTTP Server ---> Worker Pool
Client D -----/
Client E ----/
```

---

# Phase 4 — Backend HTTP Servers

Create several simple HTTP servers that will act as backend services.

Example:

```text
Backend 1 -> localhost:9001
Backend 2 -> localhost:9002
Backend 3 -> localhost:9003
```

TODO:

* [ ] Create reusable `BackendServer`.
* [ ] Configure backend host.
* [ ] Configure backend port.
* [ ] Give each backend an ID.
* [ ] Return backend ID in responses.
* [ ] Simulate different response times.
* [ ] Simulate backend failures.
* [ ] Start multiple backend instances.
* [ ] Verify each backend independently.

Example:

```text
GET /hello

Backend 1 response:

Hello from backend-1
```

---

# Phase 5 — Backend Connection

Before implementing load balancing, implement communication between two HTTP servers.

```text
HTTP Client
    |
    v
Load Balancer
    |
    | TCP connection
    v
Backend Server
```

TODO:

* [ ] Create backend connection abstraction.
* [ ] Open a `Socket` to the backend.
* [ ] Send an HTTP request.
* [ ] Read the HTTP response.
* [ ] Parse the response.
* [ ] Close the backend connection.
* [ ] Handle connection timeout.
* [ ] Handle connection failure.
* [ ] Handle malformed backend responses.

At this point the application should be capable of acting as a very simple HTTP proxy.

---

# Phase 6 — Basic Load Balancer

Now combine the HTTP server and backend connection.

```text
Client
  |
  | HTTP
  v
Load Balancer
  |
  | HTTP
  v
Backend
```

TODO:

* [ ] Create `LoadBalancer`.
* [ ] Maintain a collection of backend servers.
* [ ] Receive client request.
* [ ] Select a backend.
* [ ] Open backend connection.
* [ ] Forward request.
* [ ] Receive response.
* [ ] Forward response to client.
* [ ] Handle backend connection errors.
* [ ] Return appropriate HTTP errors.

First real load balancer milestone:

```text
curl http://localhost:8080/hello
```

should produce:

```text
Client
   |
   v
Load Balancer :8080
   |
   v
Backend :9001
```

---

# Phase 7 — Load Balancing Strategies

Create:

```java
interface LoadBalancingStrategy {

    BackendServer select(List<BackendServer> servers);

}
```

Implement multiple strategies.

## Round Robin

```text
Request 1 -> Backend 1
Request 2 -> Backend 2
Request 3 -> Backend 3
Request 4 -> Backend 1
Request 5 -> Backend 2
```

TODO:

* [ ] Implement `RoundRobinStrategy`.
* [ ] Maintain selection state.
* [ ] Handle server removal.
* [ ] Handle unavailable servers.
* [ ] Make selection thread-safe.

## Random

```text
Request 1 -> Backend 3
Request 2 -> Backend 1
Request 3 -> Backend 1
Request 4 -> Backend 2
```

TODO:

* [ ] Implement `RandomStrategy`.
* [ ] Test distribution.

## Least Connections

Track active connections:

```text
Backend 1 -> 10
Backend 2 -> 4
Backend 3 -> 7
```

Next request:

```text
Request -> Backend 2
```

TODO:

* [ ] Track active connections.
* [ ] Increment when request starts.
* [ ] Decrement when request finishes.
* [ ] Implement `LeastConnectionsStrategy`.
* [ ] Make connection counters thread-safe.
* [ ] Test concurrent requests.

---

# Phase 8 — Backend Health

The load balancer should not send traffic to unavailable servers.

```text
Backend 1 -> UP
Backend 2 -> DOWN
Backend 3 -> UP
```

Only Backend 1 and Backend 3 should receive traffic.

TODO:

* [ ] Create `ServerStatus`.
* [ ] Add `UP` state.
* [ ] Add `DOWN` state.
* [ ] Implement `/health` endpoint.
* [ ] Create `HealthChecker`.
* [ ] Periodically check backends.
* [ ] Mark failed servers as `DOWN`.
* [ ] Exclude DOWN servers from selection.
* [ ] Detect recovered servers.
* [ ] Mark recovered servers as `UP`.
* [ ] Handle all backends being unavailable.

Architecture:

```text
                 +--> Backend 1 UP
                 |
Load Balancer ---+--> Backend 2 DOWN
                 |
                 +--> Backend 3 UP
```

---

# Phase 9 — Rate Limiter

This is the main focus of the project.

The rate limiter must run before backend selection.

```text
Client
  |
  v
Load Balancer
  |
  v
Rate Limiter
  |
  +----> Reject -> HTTP 429
  |
  v
Load Balancing Strategy
  |
  v
Backend
```

Create:

```java
interface RateLimiter {

    boolean allow(String clientId);

}
```

TODO:

* [ ] Create `RateLimiter`.
* [ ] Define client identity.
* [ ] Create per-client state.
* [ ] Configure maximum requests.
* [ ] Configure time window.
* [ ] Reject requests exceeding the limit.
* [ ] Return `429 Too Many Requests`.
* [ ] Add `Retry-After`.
* [ ] Test multiple clients.
* [ ] Test concurrent clients.

---

# Phase 10 — Client Identification

Initially identify clients using their IP address.

TODO:

* [ ] Obtain remote address from `Socket`.
* [ ] Extract client IP.
* [ ] Pass IP to the rate limiter.
* [ ] Maintain independent limits per client.

Example:

```text
Client A -> 192.168.1.10
Client B -> 192.168.1.20

Client A:
5 requests/sec

Client B:
5 requests/sec
```

Later:

* [ ] Support API key.
* [ ] Support custom HTTP header.
* [ ] Support global limits.

---

# Phase 11 — Fixed Window Rate Limiter

Example:

```text
Limit: 5 requests
Window: 1 second
```

TODO:

* [ ] Store request count per client.
* [ ] Store window start timestamp.
* [ ] Reset expired windows.
* [ ] Reject requests above the limit.
* [ ] Return HTTP `429`.
* [ ] Add `Retry-After`.
* [ ] Test exactly at the limit.
* [ ] Test immediately after the limit.
* [ ] Test window expiration.
* [ ] Test multiple clients.
* [ ] Test concurrent requests.

Example:

```text
Request 1 -> 200
Request 2 -> 200
Request 3 -> 200
Request 4 -> 200
Request 5 -> 200
Request 6 -> 429
```

---

# Phase 12 — Sliding Window Rate Limiter

Instead of fixed windows, maintain request timestamps.

Possible structure:

```text
Map<ClientId, Deque<Timestamp>>
```

TODO:

* [ ] Store request timestamps.
* [ ] Remove expired timestamps.
* [ ] Count timestamps inside the current window.
* [ ] Reject when limit is reached.
* [ ] Add new timestamp when request is accepted.
* [ ] Make state thread-safe.
* [ ] Test burst traffic.
* [ ] Test boundary conditions.
* [ ] Compare against Fixed Window.

---

# Phase 13 — Token Bucket Rate Limiter

This should be the most important rate limiter implementation.

Example configuration:

```text
Capacity: 10 tokens
Refill rate: 5 tokens/sec
```

Conceptually:

```text
             refill
               |
               v
        +-------------+
        | o o o o o o |
        |   Bucket    |
        +------+------+
               |
            request
               |
        consume token
```

TODO:

* [ ] Create `TokenBucket`.
* [ ] Store capacity.
* [ ] Store current tokens.
* [ ] Store refill rate.
* [ ] Store last refill timestamp.
* [ ] Calculate elapsed time.
* [ ] Calculate generated tokens.
* [ ] Cap tokens at capacity.
* [ ] Consume a token.
* [ ] Reject when no token is available.
* [ ] Make implementation thread-safe.
* [ ] Test burst traffic.
* [ ] Test sustained traffic.
* [ ] Test concurrent requests.
* [ ] Test multiple clients.

---

# Phase 14 — Rate Limiter + Real HTTP Traffic

Connect the rate limiter to the actual HTTP server.

TODO:

* [ ] Extract client IP.
* [ ] Call `RateLimiter.allow()`.
* [ ] Reject with `429`.
* [ ] Do not select a backend for rejected requests.
* [ ] Do not open a backend connection for rejected requests.
* [ ] Add rate-limit response headers.
* [ ] Verify rejected requests never reach the backend.

Expected flow:

```text
Client
  |
  v
HTTP Request
  |
  v
Rate Limiter
  |
  +---- rejected ----> 429
  |
  v
Load Balancer
  |
  v
Backend
```

---

# Phase 15 — Concurrency and Thread Safety

Stress the system with concurrent requests.

TODO:

* [ ] Generate concurrent HTTP requests.
* [ ] Use `ExecutorService`.
* [ ] Test multiple clients.
* [ ] Test one client making many concurrent requests.
* [ ] Identify race conditions.
* [ ] Make rate limiter state thread-safe.
* [ ] Make backend connection counters thread-safe.
* [ ] Make load balancing selection thread-safe.
* [ ] Test `ConcurrentHashMap`.
* [ ] Test `AtomicInteger`.
* [ ] Compare `synchronized` and `Lock`.
* [ ] Avoid unnecessary synchronization.

Important scenario:

```text
1 client
1000 concurrent requests
```

Another:

```text
100 clients
1000 total requests
5 backends
```

---

# Phase 16 — Connection Management

Improve the networking layer.

TODO:

* [ ] Configure client connection timeout.
* [ ] Configure backend connection timeout.
* [ ] Configure socket read timeout.
* [ ] Handle client disconnects.
* [ ] Handle backend disconnects.
* [ ] Handle malformed HTTP.
* [ ] Handle slow backends.
* [ ] Return appropriate HTTP errors.
* [ ] Investigate HTTP keep-alive.
* [ ] Support multiple requests per connection.
* [ ] Understand when connections should be closed.

---

# Phase 17 — Failure Handling

Simulate real failures.

TODO:

* [ ] Backend refuses connection.
* [ ] Backend crashes.
* [ ] Backend becomes slow.
* [ ] Backend disconnects.
* [ ] Backend returns invalid HTTP.
* [ ] All backends are unavailable.
* [ ] Client disconnects during request.
* [ ] Rate limiter encounters invalid state.
* [ ] Retry another backend when appropriate.

Example:

```text
Backend 1 -> DOWN
Backend 2 -> UP
Backend 3 -> UP

Request
   |
   v
Load Balancer
   |
   +--> Backend 2
```

---

# Phase 18 — Metrics

Add basic in-memory metrics.

Track:

* [ ] Total requests.
* [ ] Successful requests.
* [ ] Failed requests.
* [ ] Rate-limited requests.
* [ ] Requests per backend.
* [ ] Active connections.
* [ ] Average response time.
* [ ] Backend failures.
* [ ] Rate-limit rejections.

Example:

```text
Total requests:       10,000
Successful:             7,500
Rate limited:            2,000
Backend failures:          500

Backend 1:              2,500
Backend 2:              2,400
Backend 3:              2,600
```

---

# Phase 19 — Testing

## Unit Tests

* [ ] HTTP parser.
* [ ] HTTP response serialization.
* [ ] Round Robin.
* [ ] Random.
* [ ] Least Connections.
* [ ] Fixed Window.
* [ ] Sliding Window.
* [ ] Token Bucket.
* [ ] Backend health state.

## HTTP Tests

* [ ] HTTP request reaches server.
* [ ] HTTP response reaches client.
* [ ] `GET` works.
* [ ] Invalid request returns `400`.
* [ ] Unknown route returns `404`.
* [ ] Rate-limited request returns `429`.
* [ ] Backend response reaches client.

## Load Balancer Tests

* [ ] Request reaches selected backend.
* [ ] Round Robin distributes requests.
* [ ] DOWN backends are ignored.
* [ ] Backend failure is handled.
* [ ] Multiple clients work simultaneously.

## Concurrency Tests

* [ ] Concurrent HTTP requests.
* [ ] Concurrent rate-limit checks.
* [ ] Concurrent backend selection.
* [ ] Concurrent backend state changes.

---

# Phase 20 — Load Testing

Create a small Java load generator.

```text
             Load Generator
                    |
                    | HTTP
                    v
             Load Balancer
                    |
          +---------+---------+
          |         |         |
          v         v         v
         S1        S2        S3
```

TODO:

* [ ] Configure number of clients.
* [ ] Configure requests per client.
* [ ] Configure concurrency.
* [ ] Configure request rate.
* [ ] Measure latency.
* [ ] Measure throughput.
* [ ] Measure rejection rate.
* [ ] Compare load balancing strategies.
* [ ] Compare rate limiter algorithms.

---

# Phase 21 — Advanced Networking

Only after the blocking implementation is fully understood.

Investigate Java NIO.

TODO:

* [ ] Learn `ServerSocketChannel`.
* [ ] Learn `SocketChannel`.
* [ ] Learn `Selector`.
* [ ] Understand blocking vs non-blocking I/O.
* [ ] Implement a basic NIO HTTP server.
* [ ] Compare it with the blocking implementation.
* [ ] Measure performance differences.
* [ ] Investigate connection scalability.

This phase is optional.

---

# Project Structure

```text
src/
├── main/
│   └── java/
│       └── com/
│           └── projects/
│               └── loadbalancer/
│
│                   ├── LoadBalancer.java
│                   │
│                   ├── http/
│                   │   ├── HttpServer.java
│                   │   ├── HttpRequest.java
│                   │   ├── HttpResponse.java
│                   │   └── HttpParser.java
│                   │
│                   ├── network/
│                   │   ├── ClientConnection.java
│                   │   └── BackendConnection.java
│                   │
│                   ├── backend/
│                   │   ├── BackendServer.java
│                   │   ├── ServerStatus.java
│                   │   └── HealthChecker.java
│                   │
│                   ├── strategy/
│                   │   ├── LoadBalancingStrategy.java
│                   │   ├── RoundRobinStrategy.java
│                   │   ├── RandomStrategy.java
│                   │   └── LeastConnectionsStrategy.java
│                   │
│                   ├── ratelimit/
│                   │   ├── RateLimiter.java
│                   │   ├── FixedWindowRateLimiter.java
│                   │   ├── SlidingWindowRateLimiter.java
│                   │   └── TokenBucketRateLimiter.java
│                   │
│                   ├── metrics/
│                   │   └── Metrics.java
│                   │
│                   └── simulation/
│                       └── LoadGenerator.java
│
└── test/
    └── java/
        └── com/
            └── projects/
                └── loadbalancer/
```

---

# Constraints

* [ ] Use Vanilla Java.
* [ ] No Spring.
* [ ] No Netty.
* [ ] No Undertow.
* [ ] No external HTTP server.
* [ ] No external rate limiter library.
* [ ] No Redis.
* [ ] No external load balancing library.
* [ ] Keep core functionality in-memory.
* [ ] Use `ServerSocket` and `Socket` for the initial HTTP implementation.
* [ ] Do not use `HttpServer` to hide the networking implementation.
* [ ] Do not optimize before the basic implementation works.
* [ ] Understand the blocking implementation before moving to NIO.

---

# Final Architecture

```text
                         HTTP Clients
                              |
                              v
                   +----------------------+
                   |     HTTP Server      |
                   |     ServerSocket     |
                   +----------+-----------+
                              |
                              v
                   +----------------------+
                   |    Load Balancer     |
                   +----------+-----------+
                              |
                              v
                   +----------------------+
                   |    Rate Limiter      |
                   |                      |
                   | Fixed Window         |
                   | Sliding Window       |
                   | Token Bucket         |
                   +----------+-----------+
                              |
                         allowed?
                        /       \
                      NO         YES
                      |           |
                    429           v
                          +-------------------+
                          | Load Balancing    |
                          | Strategy          |
                          +---------+---------+
                                    |
                     +--------------+--------------+
                     |              |              |
                     v              v              v
                 Backend 1      Backend 2      Backend 3
                     |              |              |
                     +--------------+--------------+
                                    |
                                    v
                              HTTP Response
                                    |
                                    v
                                  Client
```

---

# Learning Path

```text
HTTP
  ↓
ServerSocket / Socket
  ↓
HTTP Parsing
  ↓
HTTP Response
  ↓
Concurrent HTTP Server
  ↓
Backend HTTP Servers
  ↓
HTTP Proxying
  ↓
Load Balancing
  ↓
Health Checks
  ↓
Rate Limiting
  ↓
Fixed Window
  ↓
Sliding Window
  ↓
Token Bucket
  ↓
Concurrency
  ↓
Thread Safety
  ↓
Performance
  ↓
NIO
```

# Final Objective

The final application should be capable of receiving real HTTP traffic and performing the complete request lifecycle:

```text
HTTP Client
     |
     v
TCP Connection
     |
     v
HTTP Parsing
     |
     v
Rate Limiting
     |
     v
Backend Selection
     |
     v
Backend TCP Connection
     |
     v
HTTP Request Forwarding
     |
     v
Backend Response
     |
     v
HTTP Response Forwarding
     |
     v
HTTP Client
```

The most important component of the project is the **rate limiter**, but the load balancer provides a realistic environment in which to implement and test it.

The project should remain intentionally small and understandable, with complexity added only when the previous phase is working correctly.
