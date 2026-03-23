package com.example.reservation.integration.pricing;

import java.math.BigDecimal;
import ru.tinkoff.kora.json.common.annotation.Json;

@Json
public record PriceQuote(BigDecimal amount, String currency) {}
