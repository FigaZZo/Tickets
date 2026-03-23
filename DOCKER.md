# Docker Run

The project now supports a single-command local environment:

```bash
docker-compose up --build
```

What starts:

- `postgres` on `localhost:5432`
- `kafka` on `localhost:9094`
- `app` on `localhost:8080`
- private app endpoints on `localhost:8085`

Public API:

- `POST /reservations`
- `GET /reservations/{id}`
- `POST /reservations/{id}/confirm`
- `POST /reservations/{id}/cancel`
- `GET /events/{eventId}/availability`

Private endpoints:

- `GET http://localhost:8085/system/readiness`
- `GET http://localhost:8085/system/liveness`
- `GET http://localhost:8085/metrics`

Example create request:

```bash
curl -X POST http://localhost:8080/reservations \
  -H "Content-Type: application/json" \
  -d '{"eventId":"concert-1","seatNumber":"A1","customerId":"cust-1"}'
```

Example publish payment event from the Kafka container:

```bash
docker-compose exec kafka /opt/kafka/bin/kafka-console-producer.sh \
  --bootstrap-server kafka:9092 \
  --topic payment-events
```

Then send one JSON line:

```json
{"paymentEventId":"11111111-1111-1111-1111-111111111111","reservationId":"PUT_RESERVATION_ID_HERE","type":"CAPTURED","amount":12.34,"occurredAt":"2026-03-23T20:00:00Z"}
```

Notes:

- Kafka is exposed to the host on `localhost:9094`. Inside Compose, services use `kafka:9092`.
- The application container uses `docker/application.yaml`, so local non-Docker runs still keep the original `src/main/resources/application.yaml`.
- The Docker build uses the official Gradle build image, so it does not depend on `gradlew` working correctly inside Linux containers.
