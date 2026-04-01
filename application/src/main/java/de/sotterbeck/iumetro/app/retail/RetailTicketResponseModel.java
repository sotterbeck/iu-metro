package de.sotterbeck.iumetro.app.retail;

import de.sotterbeck.iumetro.app.ticket.TicketConfig;

import java.util.List;

public record RetailTicketResponseModel(
        String id,
        String name,
        String description,
        long priceCents,
        TicketConfig.Config config,
        boolean isActive,
        String createdAt,
        String category
) {

    public RetailTicketResponseModel {
        if (config == null) {
            config = new TicketConfig.Config(List.of());
        }
    }

}
