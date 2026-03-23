package com.example.reservation.messaging;

import ru.tinkoff.kora.kafka.common.annotation.KafkaPublisher;

@KafkaPublisher("kafka.reservation-producer")
public interface ReservationEventPublisher {
    @KafkaPublisher.Topic("reservation-events")
    void publish(ReservationEvent event);
}
