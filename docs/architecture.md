# Architecture

## Services

`producer-service` accepts log events over REST and publishes them to Kafka.

`processor-service` consumes raw log events, validates required fields, normalizes payloads, enriches logs with a fingerprint, and publishes processed logs. Invalid logs are routed to a dead-letter topic.

`analyzer-service` consumes processed log events and publishes alert events when a service reaches the configured error threshold inside the configured time window.

## Kafka Topics

- `raw-logs`: original events accepted by the ingestion API.
- `processed-logs`: validated and enriched events.
- `dead-letter-logs`: invalid events that should not block the main pipeline.
- `alert-events`: analyzer notifications such as high error-rate alerts.

## Partitioning

Kafka messages use `serviceName` as the key. This keeps logs from the same service ordered within a partition and allows horizontal scaling with consumer groups.
