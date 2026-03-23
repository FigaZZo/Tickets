package com.example.reservation.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.sql.DataSource;
import com.example.reservation.domain.Reservation;
import com.example.reservation.domain.ReservationStatus;
import ru.tinkoff.kora.common.Component;

@Component
public final class ReservationRepository {
    private final DataSource dataSource;

    public ReservationRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Reservation insert(Reservation reservation) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 insert into reservations(
                     id, event_id, seat_number, customer_id, status, amount, currency, created_at, updated_at, expires_at
                 ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                 """)) {
            bindReservation(statement, reservation);
            statement.executeUpdate();
            return reservation;
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to insert reservation", e);
        }
    }

    public Optional<Reservation> findById(UUID id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 select id, event_id, seat_number, customer_id, status, amount, currency, created_at, updated_at, expires_at
                 from reservations
                 where id = ?
                 """)) {
            statement.setObject(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(mapReservation(resultSet)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to load reservation", e);
        }
    }

    public boolean hasActiveReservation(String eventId, String seatNumber) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 select 1
                 from reservations
                 where event_id = ?
                   and seat_number = ?
                   and status in ('PENDING_PAYMENT', 'PAID')
                 fetch first 1 row only
                 """)) {
            statement.setString(1, eventId);
            statement.setString(2, seatNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to check active seat reservation", e);
        }
    }

    public boolean updateStatus(UUID id, ReservationStatus currentStatus, ReservationStatus nextStatus, OffsetDateTime updatedAt) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 update reservations
                 set status = ?, updated_at = ?
                 where id = ? and status = ?
                 """)) {
            statement.setString(1, nextStatus.name());
            statement.setObject(2, updatedAt);
            statement.setObject(3, id);
            statement.setString(4, currentStatus.name());
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to update reservation status", e);
        }
    }

    public List<Reservation> findExpiredPendingReservations(OffsetDateTime threshold) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 select id, event_id, seat_number, customer_id, status, amount, currency, created_at, updated_at, expires_at
                 from reservations
                 where status = 'PENDING_PAYMENT' and expires_at <= ?
                 """)) {
            statement.setObject(1, threshold);
            try (ResultSet resultSet = statement.executeQuery()) {
                var reservations = new ArrayList<Reservation>();
                while (resultSet.next()) {
                    reservations.add(mapReservation(resultSet));
                }
                return reservations;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to load expired reservations", e);
        }
    }

    public List<String> findReservedSeats(String eventId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 select seat_number
                 from reservations
                 where event_id = ?
                   and status in ('PENDING_PAYMENT', 'PAID')
                 order by seat_number
                 """)) {
            statement.setString(1, eventId);
            try (ResultSet resultSet = statement.executeQuery()) {
                var seats = new ArrayList<String>();
                while (resultSet.next()) {
                    seats.add(resultSet.getString("seat_number"));
                }
                return seats;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to load reserved seats", e);
        }
    }

    public int countActiveReservations(String eventId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 select count(*)
                 from reservations
                 where event_id = ?
                   and status in ('PENDING_PAYMENT', 'PAID')
                 """)) {
            statement.setString(1, eventId);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to count active reservations", e);
        }
    }

    private static Reservation mapReservation(ResultSet resultSet) throws SQLException {
        return new Reservation(
            resultSet.getObject("id", UUID.class),
            resultSet.getString("event_id"),
            resultSet.getString("seat_number"),
            resultSet.getString("customer_id"),
            ReservationStatus.valueOf(resultSet.getString("status")),
            resultSet.getBigDecimal("amount"),
            resultSet.getString("currency"),
            resultSet.getObject("created_at", OffsetDateTime.class),
            resultSet.getObject("updated_at", OffsetDateTime.class),
            resultSet.getObject("expires_at", OffsetDateTime.class)
        );
    }

    private static void bindReservation(PreparedStatement statement, Reservation reservation) throws SQLException {
        statement.setObject(1, reservation.id());
        statement.setString(2, reservation.eventId());
        statement.setString(3, reservation.seatNumber());
        statement.setString(4, reservation.customerId());
        statement.setString(5, reservation.status().name());
        statement.setBigDecimal(6, reservation.amount());
        statement.setString(7, reservation.currency());
        statement.setObject(8, reservation.createdAt());
        statement.setObject(9, reservation.updatedAt());
        statement.setObject(10, reservation.expiresAt());
    }
}
