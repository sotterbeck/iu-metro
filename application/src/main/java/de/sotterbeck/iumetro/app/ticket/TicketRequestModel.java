package de.sotterbeck.iumetro.app.ticket;

import java.util.List;
import java.util.UUID;

public record TicketRequestModel(UUID id, String name, TicketConfig.Config config) {

    public TicketRequestModel {
        if (config == null) {
            config = new TicketConfig.Config(List.of());
        }
    }
}
