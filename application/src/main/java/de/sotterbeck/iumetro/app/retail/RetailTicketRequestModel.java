package de.sotterbeck.iumetro.app.retail;

import de.sotterbeck.iumetro.app.ticket.TicketConfig;

public record RetailTicketRequestModel(
        String id,
        String name,
        String description,
        long priceCents,
        TicketConfig config,
        boolean isActive,
        String category
) {

}
