package de.sotterbeck.iumetro.domain.reader;

import de.sotterbeck.iumetro.domain.station.Station;
import de.sotterbeck.iumetro.domain.ticket.UsageType;

import java.time.LocalDateTime;

public interface TicketReaderInfo {

    Station station();

    LocalDateTime time();

    UsageType usageType();

}
