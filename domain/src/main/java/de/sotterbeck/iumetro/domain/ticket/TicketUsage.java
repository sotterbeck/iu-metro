package de.sotterbeck.iumetro.domain.ticket;

import de.sotterbeck.iumetro.domain.station.Station;

import java.time.LocalDateTime;

public record TicketUsage(Station station, LocalDateTime time, UsageType usageType) {

}
