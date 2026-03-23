package com.example.reservation.service;

import java.time.OffsetDateTime;
import com.example.reservation.domain.Reservation;
import com.example.reservation.messaging.ReservationEvent;
import com.example.reservation.messaging.ReservationEventPublisher;
import ru.tinkoff.kora.common.Component;

@Component
public final class ReservationEventService {
    private final ReservationEventPublisher publisher;

    public ReservationEventService(ReservationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publishCreated(Reservation reservation) {
        publisher.publish(toEvent("ReservationCreated", reservation));
    }

    public void publishConfirmed(Reservation reservation) {
        publisher.publish(toEvent("ReservationConfirmed", reservation));
    }

    public void publishCancelled(Reservation reservation) {
        publisher.publish(toEvent("ReservationCancelled", reservation));
    }

    public void publishExpired(Reservation reservation) {
        publisher.publish(toEvent("ReservationExpired", reservation));
    }

    private static ReservationEvent toEvent(String eventType, Reservation reservation) {
        return new ReservationEvent(
            reservation.id(),
            eventType,
            reservation.eventId(),
            reservation.seatNumber(),
            reservation.customerId(),
            reservation.status(),
            reservation.amount(),
            reservation.currency(),
            OffsetDateTime.now()
        );
    }
}
