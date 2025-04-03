package de.sotterbeck.iumetro.app.ticket;

import java.time.Duration;
import java.util.UUID;

public record TicketDto(UUID id, String name, int usageLimit, Duration timeLimit) {

    public TicketDto(UUID id, String name) {
        this(id, name, 0, Duration.ZERO);
    }

}
