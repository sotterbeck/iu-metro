package de.sotterbeck.iumetro.app.ticket;

import java.util.List;

public record TicketResponseModel(
        String fullId,
        String shortId,
        String name,
        TicketConfig.Config config
) {

    public TicketResponseModel {
        if (config == null) {
            config = new TicketConfig.Config(List.of());
        }
    }

}
