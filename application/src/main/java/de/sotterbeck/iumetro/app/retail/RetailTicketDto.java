package de.sotterbeck.iumetro.app.retail;

import de.sotterbeck.iumetro.app.ticket.TicketConfig;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record RetailTicketDto(
        UUID id,
        String name,
        String description,
        long priceCents,
        TicketConfig config,
        boolean isActive,
        Instant createdAt,
        String category
) {

    public RetailTicketDto {
        if (config == null) {
            config = new TicketConfig(List.of());
        }
    }

}
