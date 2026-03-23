package com.example.reservation.api;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import com.example.reservation.domain.Reservation;
import ru.tinkoff.kora.json.common.annotation.Json;

@Json
public record ReservationResponse(
    UUID id,
    String eventId,
    String seatNumber,
    String customerId,
    String status,
    BigDecimal amount,
    String currency,
    OffsetDateTime createdAt,
    OffsetDateTime expiresAt
) {
    public static ReservationResponse fromReservation(Reservation reservation) {
        return new ReservationResponse(
            reservation.id(),
            reservation.eventId(),
            reservation.seatNumber(),
            reservation.customerId(),
            reservation.status().name(),
            reservation.amount(),
            reservation.currency(),
            reservation.createdAt(),
            reservation.expiresAt()
        );
    }
}
