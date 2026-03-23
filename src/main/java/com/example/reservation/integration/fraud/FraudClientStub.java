package com.example.reservation.integration.fraud;

import ru.tinkoff.kora.common.Component;

@Component
public final class FraudClientStub implements FraudClient {
    @Override
    public FraudCheckResult check(String eventId, String seatNumber, String customerId) {
        var approved = !customerId.toLowerCase().contains("fraud");
        return new FraudCheckResult(approved, approved ? "approved" : "customer flagged");
    }
}
