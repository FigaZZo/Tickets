package com.example.reservation.integration.pricing;

import ru.tinkoff.kora.common.Component;
import ru.tinkoff.kora.resilient.retry.annotation.Retry;
import ru.tinkoff.kora.resilient.timeout.annotation.Timeout;

@Component
public class PricingFacade {
    private final PricingClient pricingClient;

    public PricingFacade(PricingClient pricingClient) {
        this.pricingClient = pricingClient;
    }

    @Timeout("pricing")
    @Retry("pricing")
    public PriceQuote quote(String eventId, String seatNumber) {
        return pricingClient.quote(eventId, seatNumber);
    }
}
