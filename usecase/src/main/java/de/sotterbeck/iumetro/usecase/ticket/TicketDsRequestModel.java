package de.sotterbeck.iumetro.usecase.ticket;

import java.time.Duration;
import java.util.UUID;

public record TicketDsRequestModel(UUID id, String name, int usageLimit, Duration timeLimit) {

    public TicketDsRequestModel(UUID id, String name) {
        this(id, name, 0, Duration.ZERO);
    }

}
