package de.sotterbeck.iumetro.domain.ticket;

import java.time.ZonedDateTime;

public record TicketUsage(ZonedDateTime time, UsageType usageType) {

}
