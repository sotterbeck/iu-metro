package de.sotterbeck.iumetro.app.ticket;

import java.time.Duration;
import java.util.UUID;

public record TicketRequestModel(UUID id, String name, int usageLimit, Duration timeLimit) {

}
