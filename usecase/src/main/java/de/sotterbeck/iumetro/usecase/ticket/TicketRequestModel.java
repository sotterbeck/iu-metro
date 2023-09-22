package de.sotterbeck.iumetro.usecase.ticket;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

public record TicketRequestModel(UUID id, String name, BigDecimal price, int usageLimit, Duration timeLimit) {

}
