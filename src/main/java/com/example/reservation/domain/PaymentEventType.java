package com.example.reservation.domain;

import ru.tinkoff.kora.json.common.annotation.Json;

@Json
public enum PaymentEventType {
    CAPTURED,
    FAILED
}
