package com.example.reservation.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record Reservation(
    UUID id,
    String eventId,
    String seatNumber,
    String customerId,
    ReservationStatus status,
    BigDecimal amount,
    String currency,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt,
    OffsetDateTime expiresAt
) {}
