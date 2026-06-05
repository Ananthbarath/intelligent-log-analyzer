# Intelligent Log Analyzer

Java + Kafka backend project for ingesting application logs, processing them through a streaming pipeline, and preparing them for search, alerting, and analytics.

## Current Scope

This first implementation includes:

- Maven multi-module project
- Shared log event contracts
- Kafka topic constants
- `producer-service` for REST log ingestion
- `processor-service` for validation, enrichment, and dead-letter routing
- Docker Compose for Kafka, PostgreSQL, and OpenSearch
- Architecture and API documentation

## Architecture

```text
Client / Postman
      |
      v
producer-service
      |
      v
Kafka: raw-logs
      |
      v
processor-service
      |
      +--> Kafka: processed-logs
      |
      +--> Kafka: dead-letter-logs
```

Next services will add alert detection, OpenSearch indexing, PostgreSQL alert storage, and query APIs.

## Tech Stack

- Java 17
- Spring Boot 3
- Apache Kafka
- PostgreSQL
- OpenSearch
- Docker Compose
- JUnit 5 + Mockito

## Run

Start infrastructure:

```powershell
docker compose up -d
```

Run producer:

```powershell
mvn spring-boot:run -pl producer-service
```

Run processor:

```powershell
mvn spring-boot:run -pl processor-service
```

Submit a log:

```http
POST http://localhost:8081/api/logs
Content-Type: application/json
```

```json
{
  "serviceName": "payment-service",
  "level": "ERROR",
  "message": "Gateway timeout while charging card",
  "traceId": "trace-1001",
  "userId": "user-42",
  "host": "app-01"
}
```

## Development Roadmap

1. Scaffold producer and processor pipeline.
2. Add analyzer service with high error rate detection.
3. Store alerts in PostgreSQL.
4. Index processed logs in OpenSearch.
5. Add query APIs for logs and alerts.
6. Add integration tests with Testcontainers.
7. Add load testing and observability notes.
