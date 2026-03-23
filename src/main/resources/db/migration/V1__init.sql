create table if not exists reservations (
    id uuid primary key,
    event_id varchar(128) not null,
    seat_number varchar(64) not null,
    customer_id varchar(128) not null,
    status varchar(32) not null,
    amount numeric(12, 2) not null,
    currency varchar(3) not null,
    created_at timestamptz not null,
    updated_at timestamptz not null,
    expires_at timestamptz not null
);

create unique index if not exists uq_reservations_active_seat
    on reservations(event_id, seat_number)
    where status in ('PENDING_PAYMENT', 'PAID');

create table if not exists processed_payment_events (
    payment_event_id uuid primary key,
    reservation_id uuid not null,
    event_type varchar(32) not null,
    amount numeric(12, 2) not null,
    occurred_at timestamptz not null
);
