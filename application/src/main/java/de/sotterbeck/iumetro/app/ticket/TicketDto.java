package de.sotterbeck.iumetro.app.ticket;

import java.util.List;
import java.util.UUID;

public record TicketDto(UUID id, String name, TicketConfig.Config config) {

    public TicketDto {
        if (config == null) {
            config = new TicketConfig.Config(List.of());
        }
    }
}
