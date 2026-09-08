# Consistent Hashing

An in-process Java library that models a consistent-hash ring with virtual nodes, replication, and rebalancing.

It is a learning implementation of ideas used by systems such as Cassandra, DynamoDB, and Redis Cluster. There is no network layer: nodes and storage live in the same JVM.

## What it does

- Places keys on a hash ring using SHA-256
- Maps each physical node to several virtual nodes
- Looks up the clockwise successor for a key
- Replicates values across a configurable number of nodes
- Moves keys when a node joins or leaves (`Rebalancer`)
- Stores data in memory (`ConcurrentHashMap`)

A small `App` class demonstrates put/get on a four-node ring.

## What it does not do

Gossip, quorum reads/writes, disk persistence, or RPC between nodes.

## Stack

- Java 17
- Maven
- JUnit 5

## Run

```bash
mvn test
mvn exec:java -Dexec.mainClass=com.projects.App
```
