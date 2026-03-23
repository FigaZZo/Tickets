package com.example.reservation.messaging;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import com.example.reservation.domain.PaymentEventType;
import ru.tinkoff.kora.json.common.annotation.Json;

@Json
public record PaymentEvent(
    UUID paymentEventId,
    UUID reservationId,
    PaymentEventType type,
    BigDecimal amount,
    OffsetDateTime occurredAt
) {}
