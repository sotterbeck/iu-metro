package de.sotterbeck.iumetro.usecase.ticket;

import java.time.Duration;
import java.util.UUID;

public record TicketDsModel(UUID id, String name, int usageLimit, Duration timeLimit) {

    public TicketDsModel(UUID id, String name) {
        this(id, name, 0, Duration.ZERO);
    }

}
