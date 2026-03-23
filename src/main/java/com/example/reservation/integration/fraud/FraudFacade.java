package com.example.reservation.integration.fraud;

import ru.tinkoff.kora.common.Component;
import ru.tinkoff.kora.resilient.circuitbreaker.annotation.CircuitBreaker;
import ru.tinkoff.kora.resilient.retry.annotation.Retry;
import ru.tinkoff.kora.resilient.timeout.annotation.Timeout;

@Component
public class FraudFacade {
    private final FraudClient fraudClient;

    public FraudFacade(FraudClient fraudClient) {
        this.fraudClient = fraudClient;
    }

    @Timeout("fraud")
    @Retry("fraud")
    @CircuitBreaker("fraud")
    public FraudCheckResult check(String eventId, String seatNumber, String customerId) {
        return fraudClient.check(eventId, seatNumber, customerId);
    }
}
