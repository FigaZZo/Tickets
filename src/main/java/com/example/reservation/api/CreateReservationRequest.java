package com.example.reservation.api;

import ru.tinkoff.kora.json.common.annotation.Json;
import ru.tinkoff.kora.validation.common.annotation.NotBlank;

@Json
public record CreateReservationRequest(
    @NotBlank String eventId,
    @NotBlank String seatNumber,
    @NotBlank String customerId
) {}
