package com.example.reservation.service;

import java.time.OffsetDateTime;
import java.util.UUID;
import com.example.reservation.api.CreateReservationRequest;
import com.example.reservation.api.EventAvailabilityResponse;
import com.example.reservation.domain.PaymentEventType;
import com.example.reservation.domain.Reservation;
import com.example.reservation.domain.ReservationStatus;
import com.example.reservation.integration.fraud.FraudFacade;
import com.example.reservation.integration.pricing.PricingFacade;
import com.example.reservation.messaging.PaymentEvent;
import com.example.reservation.repository.PaymentEventRepository;
import com.example.reservation.repository.ReservationRepository;
import ru.tinkoff.kora.common.Component;
import ru.tinkoff.kora.validation.common.annotation.Validate;

@Component
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final PaymentEventRepository paymentEventRepository;
    private final PricingFacade pricingFacade;
    private final FraudFacade fraudFacade;
    private final ReservationEventService reservationEventService;

    public ReservationService(
        ReservationRepository reservationRepository,
        PaymentEventRepository paymentEventRepository,
        PricingFacade pricingFacade,
        FraudFacade fraudFacade,
        ReservationEventService reservationEventService
    ) {
        this.reservationRepository = reservationRepository;
        this.paymentEventRepository = paymentEventRepository;
        this.pricingFacade = pricingFacade;
        this.fraudFacade = fraudFacade;
        this.reservationEventService = reservationEventService;
    }

    @Validate
    public Reservation create(CreateReservationRequest request) {
        requireText(request.eventId(), "eventId");
        requireText(request.seatNumber(), "seatNumber");
        requireText(request.customerId(), "customerId");
        if (reservationRepository.hasActiveReservation(request.eventId(), request.seatNumber())) {
            throw new IllegalStateException("Seat is already reserved");
        }

        var quote = pricingFacade.quote(request.eventId(), request.seatNumber());
        var fraudCheck = fraudFacade.check(request.eventId(), request.seatNumber(), request.customerId());
        var now = OffsetDateTime.now();
        var reservation = new Reservation(
            UUID.randomUUID(),
            request.eventId(),
            request.seatNumber(),
            request.customerId(),
            fraudCheck.approved() ? ReservationStatus.PENDING_PAYMENT : ReservationStatus.REJECTED,
            quote.amount(),
            quote.currency(),
            now,
            now,
            now.plusMinutes(15)
        );
        var created = reservationRepository.insert(reservation);
        reservationEventService.publishCreated(created);
        return created;
    }

    public Reservation getById(UUID id) {
        return reservationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + id));
    }

    public Reservation confirmManually(UUID id) {
        var reservation = getById(id);
        transition(reservation, ReservationStatus.PENDING_PAYMENT, ReservationStatus.PAID);
        var updated = getById(id);
        reservationEventService.publishConfirmed(updated);
        return updated;
    }

    public Reservation cancel(UUID id) {
        var reservation = getById(id);
        if (reservation.status() == ReservationStatus.PAID) {
            throw new IllegalStateException("Paid reservations are immutable in MVP");
        }
        if (reservation.status() == ReservationStatus.CANCELLED || reservation.status() == ReservationStatus.EXPIRED) {
            return reservation;
        }
        transition(reservation, reservation.status(), ReservationStatus.CANCELLED);
        var updated = getById(id);
        reservationEventService.publishCancelled(updated);
        return updated;
    }

    public void handlePaymentEvent(PaymentEvent paymentEvent) {
        if (!paymentEventRepository.markProcessed(paymentEvent)) {
            return;
        }
        if (paymentEvent.type() == PaymentEventType.CAPTURED) {
            var reservation = getById(paymentEvent.reservationId());
            if (reservation.status() == ReservationStatus.PENDING_PAYMENT) {
                transition(reservation, ReservationStatus.PENDING_PAYMENT, ReservationStatus.PAID);
                reservationEventService.publishConfirmed(getById(paymentEvent.reservationId()));
            }
            return;
        }

        if (paymentEvent.type() == PaymentEventType.FAILED) {
            var reservation = getById(paymentEvent.reservationId());
            if (reservation.status() == ReservationStatus.PENDING_PAYMENT) {
                transition(reservation, ReservationStatus.PENDING_PAYMENT, ReservationStatus.CANCELLED);
                reservationEventService.publishCancelled(getById(paymentEvent.reservationId()));
            }
        }
    }

    public EventAvailabilityResponse getAvailability(String eventId) {
        return new EventAvailabilityResponse(
            eventId,
            reservationRepository.countActiveReservations(eventId),
            reservationRepository.findReservedSeats(eventId)
        );
    }

    private void transition(Reservation reservation, ReservationStatus current, ReservationStatus next) {
        var updated = reservationRepository.updateStatus(reservation.id(), current, next, OffsetDateTime.now());
        if (!updated) {
            throw new IllegalStateException(
                "Reservation " + reservation.id() + " is no longer in " + current + " state"
            );
        }
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
    }
}
