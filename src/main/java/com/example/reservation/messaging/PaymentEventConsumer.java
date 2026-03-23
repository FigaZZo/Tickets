package com.example.reservation.messaging;

import com.example.reservation.service.ReservationService;
import ru.tinkoff.kora.common.Component;
import ru.tinkoff.kora.json.common.annotation.Json;
import ru.tinkoff.kora.kafka.common.annotation.KafkaListener;

@Component
public final class PaymentEventConsumer {
    private final ReservationService reservationService;

    public PaymentEventConsumer(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @KafkaListener("kafka.payment-consumer")
    public void onPaymentEvent(@Json PaymentEvent paymentEvent) {
        reservationService.handlePaymentEvent(paymentEvent);
    }
}
