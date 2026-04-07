package de.sotterbeck.iumetro.app.retail;

import de.sotterbeck.iumetro.app.ticket.TicketConfig;

import java.util.List;

public record RetailTicketRequestModel(
        String id,
        String name,
        String description,
        long priceCents,
        TicketConfig config,
        boolean isActive,
        String category
) {

    public RetailTicketRequestModel {
        if (config == null) {
            config = new TicketConfig(List.of());
        }
    }

}
