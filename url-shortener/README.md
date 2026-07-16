# URL Shortening

A distributed, scalable URL shortener built with Spring Boot and AWS-like services using MiniStack.
This project focuses on real-world system design concepts such as caching, partitioning, asynchrounous processing, and high-concurrency handling.

# Overview
This project simulates a production grade distributed system using:

- Microservices architecture
- Distributed caching
- Database with a master/slave scheme

# System architecture

[System architecture](./arc.excalidraw)

# URL Shortening

1. Each longURL must be hashed to one hashValue
2. Each hashValue can be mamped back to the longURL

