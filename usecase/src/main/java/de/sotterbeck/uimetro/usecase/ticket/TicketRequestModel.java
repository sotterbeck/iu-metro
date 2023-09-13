package de.sotterbeck.uimetro.usecase.ticket;

import java.math.BigDecimal;
import java.time.Duration;

public record TicketRequestModel(String name, BigDecimal price, int usageLimit, Duration timeLimit) {

}
