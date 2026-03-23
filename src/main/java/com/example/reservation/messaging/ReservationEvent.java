package com.example.reservation.messaging;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import com.example.reservation.domain.ReservationStatus;
import ru.tinkoff.kora.json.common.annotation.Json;

@Json
public record ReservationEvent(
    UUID reservationId,
    String eventType,
    String eventId,
    String seatNumber,
    String customerId,
    ReservationStatus status,
    BigDecimal amount,
    String currency,
    OffsetDateTime occurredAt
) {}
