package com.example.reservation.service;

import java.time.OffsetDateTime;
import com.example.reservation.domain.ReservationStatus;
import com.example.reservation.repository.ReservationRepository;
import ru.tinkoff.kora.common.Component;

@Component
public final class ExpirationService {
    private final ReservationRepository reservationRepository;
    private final ReservationEventService reservationEventService;

    public ExpirationService(ReservationRepository reservationRepository, ReservationEventService reservationEventService) {
        this.reservationRepository = reservationRepository;
        this.reservationEventService = reservationEventService;
    }

    public void expirePendingReservations() {
        var now = OffsetDateTime.now();
        var expiredReservations = reservationRepository.findExpiredPendingReservations(now);
        for (var reservation : expiredReservations) {
            var updated = reservationRepository.updateStatus(
                reservation.id(),
                ReservationStatus.PENDING_PAYMENT,
                ReservationStatus.EXPIRED,
                now
            );
            if (updated) {
                reservationEventService.publishExpired(reservation);
            }
        }
    }
}
