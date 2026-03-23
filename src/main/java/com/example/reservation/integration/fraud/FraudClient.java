package com.example.reservation.integration.fraud;

public interface FraudClient {
    FraudCheckResult check(String eventId, String seatNumber, String customerId);
}
