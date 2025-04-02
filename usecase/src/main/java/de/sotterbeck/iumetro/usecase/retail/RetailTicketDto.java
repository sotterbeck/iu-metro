package de.sotterbeck.iumetro.usecase.retail;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public record RetailTicketDto(
        UUID id,
        String name,
        String description,
        long priceCents,
        int usageLimit,
        Duration timeLimit,
        boolean isActive,
        Instant createdAt,
        String category
) {

}
