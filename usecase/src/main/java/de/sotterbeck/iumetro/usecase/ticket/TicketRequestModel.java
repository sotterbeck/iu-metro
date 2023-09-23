package de.sotterbeck.iumetro.usecase.ticket;

import java.time.Duration;
import java.util.UUID;

public record TicketRequestModel(UUID id, String name, int usageLimit, Duration timeLimit) {

}
