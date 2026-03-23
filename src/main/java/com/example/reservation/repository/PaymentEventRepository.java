package com.example.reservation.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import com.example.reservation.messaging.PaymentEvent;
import ru.tinkoff.kora.common.Component;

@Component
public final class PaymentEventRepository {
    private final DataSource dataSource;

    public PaymentEventRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean markProcessed(PaymentEvent event) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 insert into processed_payment_events(payment_event_id, reservation_id, event_type, amount, occurred_at)
                 values (?, ?, ?, ?, ?)
                 on conflict (payment_event_id) do nothing
                 """)) {
            statement.setObject(1, event.paymentEventId());
            statement.setObject(2, event.reservationId());
            statement.setString(3, event.type().name());
            statement.setBigDecimal(4, event.amount());
            statement.setObject(5, event.occurredAt());
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to persist processed payment event", e);
        }
    }
}
