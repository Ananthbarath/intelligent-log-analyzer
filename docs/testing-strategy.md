# Testing Strategy

## Unit Tests

- Producer publishes logs using `serviceName` as the Kafka key.
- Processor rejects invalid logs.
- Processor enriches valid logs with normalized message, processed timestamp, and fingerprint.
- Analyzer emits high error-rate alerts when a service crosses the configured threshold inside the configured window.
- Analyzer ignores non-error logs and expires old errors before threshold checks.

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
5. Send enough error logs for one service to cross the analyzer threshold.
6. Verify the analyzer emits to `alert-events`.
