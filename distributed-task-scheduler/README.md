# Distributed task scheduler

**Distributed Task Scheduling System**  
This system allows users to schedule tasks that are executed automatically in a distributed, scalable, and fault-tolerant environment. 
Ideal for automation, integrations, and enterprise-level task management.

## Technologies

- **Backend:** Node.js (Express)
- **Database:** PostgreSQL  
- **Message Broker / Queue:** RabbitMQ or Kafka  
- **Containers:** Docker  
- **Orchestration:** Kubernetes  
- **Optional Logging / Monitoring:** Prometheus, Grafana

## Overview

The system enables users to define scheduled tasks of various types that execute automatically at the specified time and are distributed across multiple workers.

## Components:

1. **API Service:**  
   - Creates, lists, cancels, and updates tasks.  
   - Does not execute tasks, only records and exposes data.

2. **Scheduler Service:**  
   - Checks the database for pending tasks.  
   - Sends tasks to the queue for execution.  
   - Supports one-shot, recurring, or delayed tasks.

3. **Queue / Broker:**  
   - RabbitMQ or Kafka stores pending tasks.  
   - Workers consume tasks from the queue.

4. **Worker Service:**  
   - Executes tasks based on their type.  
   - Reports execution results and logs.  
   - Retries failed tasks and optionally moves them to a dead-letter queue.

5. **Database:**  
   - Stores `tasks`, `task_runs`, and `task_logs`.


## Supported task types

1. **HTTP Request**  
   Executes an HTTP request (GET, POST, PUT, DELETE) to any endpoint.  

   ```json
   {
     "type": "http",
     "method": "POST",
     "url": "https://myapp.com/webhook",
     "headers": { "Authorization": "Bearer 123" },
     "body": { "msg": "hello" }
   }
   ```
2. **Email**  
   Sends emails via SMTP or external providers..  

   ```json
    {
    "type": "email",
    "to": "user@example.com",
    "subject": "Reminder",
    "body": "Your event starts in 1 hour"
    }
   ```
3. Internal Action
Executes internal scripts or functions.


## Scheduling modes
One-shot: Executes once at a specific date and time.
Recurring: Executes repeatedly according to a schedule (e.g., every X minutes, daily, weekly).
Delayed: Executes a specified time after creation.


