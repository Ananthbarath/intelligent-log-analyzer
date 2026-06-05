# API Contracts

## Submit Log

`POST /api/logs`

```json
{
  "serviceName": "payment-service",
  "level": "ERROR",
  "message": "Gateway timeout",
  "traceId": "trace-1001",
  "userId": "user-42",
  "host": "app-01"
}
```

Response:

```json
{
  "id": "generated-event-id",
  "status": "accepted"
}
```
