package com.example.reservation.integration.fraud;

import ru.tinkoff.kora.json.common.annotation.Json;

@Json
public record FraudCheckResult(boolean approved, String reason) {}
