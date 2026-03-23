package com.example.reservation.domain;

import ru.tinkoff.kora.json.common.annotation.Json;

@Json
public enum ReservationStatus {
    PENDING_PAYMENT,
    PAID,
    CANCELLED,
    EXPIRED,
    REJECTED
}
