package com.example.reservation.job;

import com.example.reservation.service.ExpirationService;
import ru.tinkoff.kora.common.Component;
import ru.tinkoff.kora.scheduling.jdk.annotation.ScheduleWithFixedDelay;

@Component
public final class ExpireReservationsJob {
    private final ExpirationService expirationService;

    public ExpireReservationsJob(ExpirationService expirationService) {
        this.expirationService = expirationService;
    }

    @ScheduleWithFixedDelay(config = "reservation.expiration.job")
    public void expireReservations() {
        expirationService.expirePendingReservations();
    }
}
