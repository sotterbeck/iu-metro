package de.sotterbeck.iumetro.app.retail;

import de.sotterbeck.iumetro.app.ticket.TicketConfig;

public record RetailTicketResponseModel(
        String id,
        String name,
        String description,
        long priceCents,
        TicketConfig config,
        boolean isActive,
        String createdAt,
        String category
) {

}
