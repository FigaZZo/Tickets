package com.example.reservation.messaging;

import java.util.Map;
import org.apache.kafka.common.serialization.Serializer;
import ru.tinkoff.kora.common.Component;
import ru.tinkoff.kora.json.common.JsonWriter;

@Component
public final class ReservationEventSerializer implements Serializer<ReservationEvent> {
    private final JsonWriter<ReservationEvent> jsonWriter;

    public ReservationEventSerializer(JsonWriter<ReservationEvent> jsonWriter) {
        this.jsonWriter = jsonWriter;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, ReservationEvent data) {
        return data == null ? null : jsonWriter.toByteArrayUnchecked(data);
    }

    @Override
    public void close() {
    }
}
