# Testing Strategy

## Unit Tests

- Producer publishes logs using `serviceName` as the Kafka key.
- Processor rejects invalid logs.
- Processor enriches valid logs with normalized message, processed timestamp, and fingerprint.

## Integration Tests

Later phases should use Testcontainers for:

- Kafka
- PostgreSQL
- OpenSearch

## End-to-End Test

1. Submit a log through the producer API.
2. Verify the event reaches `raw-logs`.
3. Verify the processor emits to `processed-logs`.
4. Verify invalid input goes to `dead-letter-logs`.
