package com.example.reservation.integration.pricing;

public interface PricingClient {
    PriceQuote quote(String eventId, String seatNumber);
}
