package com.example.reservation.api;

import java.util.UUID;
import com.example.reservation.service.ReservationService;
import ru.tinkoff.kora.common.Component;
import ru.tinkoff.kora.http.common.HttpMethod;
import ru.tinkoff.kora.http.common.annotation.HttpRoute;
import ru.tinkoff.kora.http.common.annotation.Path;
import ru.tinkoff.kora.http.server.common.annotation.HttpController;
import ru.tinkoff.kora.json.common.annotation.Json;

@Component
@HttpController
public final class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @HttpRoute(method = HttpMethod.POST, path = "/reservations")
    @Json
    public ReservationResponse createReservation(@Json CreateReservationRequest request) {
        return ReservationResponse.fromReservation(reservationService.create(request));
    }

    @HttpRoute(method = HttpMethod.GET, path = "/reservations/{id}")
    @Json
    public ReservationResponse getReservation(@Path UUID id) {
        return ReservationResponse.fromReservation(reservationService.getById(id));
    }

    @HttpRoute(method = HttpMethod.POST, path = "/reservations/{id}/confirm")
    @Json
    public ReservationResponse confirmReservation(@Path UUID id) {
        return ReservationResponse.fromReservation(reservationService.confirmManually(id));
    }

    @HttpRoute(method = HttpMethod.POST, path = "/reservations/{id}/cancel")
    @Json
    public ReservationResponse cancelReservation(@Path UUID id) {
        return ReservationResponse.fromReservation(reservationService.cancel(id));
    }

    @HttpRoute(method = HttpMethod.GET, path = "/events/{eventId}/availability")
    @Json
    public EventAvailabilityResponse getAvailability(@Path String eventId) {
        return reservationService.getAvailability(eventId);
    }
}
