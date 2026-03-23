package com.example.reservation.integration.pricing;

import java.math.BigDecimal;
import ru.tinkoff.kora.common.Component;

@Component
public final class PricingClientStub implements PricingClient {
    @Override
    public PriceQuote quote(String eventId, String seatNumber) {
        var price = BigDecimal.valueOf((seatNumber.hashCode() & Integer.MAX_VALUE) % 5000L + 1000L, 2);
        return new PriceQuote(price, "USD");
    }
}
