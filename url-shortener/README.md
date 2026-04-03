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

The hash of the longUrl can contain [0-9,a-z,A-Z] = 10 + 26 + 26 characters and we will use a hash MD5 and we will cut the string on 5 characters, if we find a collition
we append a predefined string to generate another until we get a unique id for the longUrl

