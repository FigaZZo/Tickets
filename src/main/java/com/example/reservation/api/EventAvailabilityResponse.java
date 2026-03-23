package com.example.reservation.api;

import java.util.List;
import ru.tinkoff.kora.json.common.annotation.Json;

@Json
public record EventAvailabilityResponse(
    String eventId,
    int activeReservationCount,
    List<String> reservedSeats
) {}
