# Ticket Service

MVP reservation service built with Kora.

The service allows a client to:

- create a reservation for an event seat
- get reservation details
- confirm a reservation
- cancel a reservation
- check event availability
- automatically expire unpaid reservations

The project also includes:

- PostgreSQL persistence
- Kafka producer and consumer
- pricing and fraud integration facades
- scheduler-based expiration
- health and metrics endpoints
- Docker Compose environment for local run

## Stack

- Java 21
- Gradle
- Kora
- PostgreSQL
- Apache Kafka
- Flyway
- Docker / Docker Compose

## Run

Start the full local environment:

```bash
docker-compose up --build
```

Containers and ports:

- `app` -> `localhost:8080`
- private app endpoints -> `localhost:8085`
- `postgres` -> `localhost:5432`
- `kafka` -> `localhost:9094`

Inside Compose, the application connects to:

- Postgres: `postgres:5432`
- Kafka: `kafka:9092`

## Endpoints

Public API:

- `POST /reservations`
- `GET /reservations/{id}`
- `POST /reservations/{id}/confirm`
- `POST /reservations/{id}/cancel`
- `GET /events/{eventId}/availability`

Private endpoints:

- `GET /system/readiness`
- `GET /system/liveness`
- `GET /metrics`
- `GET /openapi`

## Smoke Test

Check liveness and readiness:

```powershell
Invoke-RestMethod http://localhost:8085/system/liveness
Invoke-RestMethod http://localhost:8085/system/readiness
```

Create a reservation:

```powershell
$createBody = @{
  eventId = "concert-1"
  seatNumber = "A1"
  customerId = "cust-1"
} | ConvertTo-Json

$reservation = Invoke-RestMethod `
  -Method Post `
  -Uri http://localhost:8080/reservations `
  -ContentType "application/json" `
  -Body $createBody

$reservation
```

Read it back:

```powershell
Invoke-RestMethod "http://localhost:8080/reservations/$($reservation.id)"
```

Check availability:

```powershell
Invoke-RestMethod "http://localhost:8080/events/concert-1/availability"
```

Cancel it:

```powershell
Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:8080/reservations/$($reservation.id)/cancel"
```

## Payment Event Test

Create another reservation:

```powershell
$createBody = @{
  eventId = "concert-1"
  seatNumber = "A2"
  customerId = "cust-2"
} | ConvertTo-Json

$reservation2 = Invoke-RestMethod `
  -Method Post `
  -Uri http://localhost:8080/reservations `
  -ContentType "application/json" `
  -Body $createBody
```

Publish payment event to Kafka:

```powershell
@"
{"paymentEventId":"11111111-1111-1111-1111-111111111111","reservationId":"$($reservation2.id)","type":"CAPTURED","amount":12.34,"occurredAt":"2026-03-24T12:00:00Z"}
"@ | docker-compose exec -T kafka /opt/kafka/bin/kafka-console-producer.sh --bootstrap-server kafka:9092 --topic payment-events
```

Verify that the reservation status changed:

```powershell
Invoke-RestMethod "http://localhost:8080/reservations/$($reservation2.id)"
```

Expected status after payment event: `PAID`

## OpenAPI

Specs are stored in:

- `src/main/resources/openapi/reservation-api.yaml`
- `src/main/resources/openapi/pricing-api.yaml`
- `src/main/resources/openapi/fraud-api.yaml`

OpenAPI management endpoint is exposed at:

- `http://localhost:8085/openapi`

## Project Structure

- `src/main/java/com/example/reservation/api` - HTTP layer
- `src/main/java/com/example/reservation/domain` - domain model
- `src/main/java/com/example/reservation/service` - business logic
- `src/main/java/com/example/reservation/repository` - JDBC persistence
- `src/main/java/com/example/reservation/messaging` - Kafka producer/consumer
- `src/main/java/com/example/reservation/integration` - pricing and fraud facades
- `src/main/java/com/example/reservation/job` - scheduled expiration
- `src/main/resources/db/migration` - Flyway migrations

## Notes

- This is an MVP, not a production-complete reservation platform.
- Pricing and fraud integrations are currently stubbed behind facades.
- Kafka and Postgres are required for application startup in the current setup.
- Manual confirm endpoint exists, but the more realistic confirmation path is the Kafka payment event flow.
