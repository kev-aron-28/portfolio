# Notification system

This project is a notification system focused on delivering messages through two primary channels:

- Email
- SMS

# Scope
- Sending notifications
    - Email
    - SMS
- Message templating (basic placeholders)
- Notification dispatching
- Retry mechanism for failed deliveries
- Loggin and monitoring of sent notifications
- Basic rate limiting
- Queue-based processing

# Excluded
- Push notifications
- In-app notifications
- Multi-channel orchestration logic
- Complex user preference systems
- Marketing campaings or bulk messaging tools

# Core features

1. Notification API
2. Message templates
3. Queue and asynchrounous processing
4. Retry and failure handling
5. Loggin and monitoring
6. Rate limiting

--- 
# Back of the envelope
First, we need assumptions

- When it comes to users, when can mention them in terms of internal services sending notifications: 50-100 services
- Notifications triggered by day: ~1M/day = 2^
- Peak traffic: x5 average

Throughput:
- 1M / day = 12 notifications x sec
- Peak: 60 notifications / sec

Payload size:
- Email: 5-20KB
- SMS: 200 bytes

Average: 10KB
Bandwith: 10^6 * 10^4 = 10^10= 10GB/day

Storage: 
If we store metadata : 1kb per notification

So 1M/day = ~1Gb/day
30 GB/month

Well implement a data retention strategy
