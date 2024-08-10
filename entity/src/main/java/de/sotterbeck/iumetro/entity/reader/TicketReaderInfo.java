package de.sotterbeck.iumetro.entity.reader;

import de.sotterbeck.iumetro.entity.station.Station;
import de.sotterbeck.iumetro.entity.ticket.UsageType;

import java.time.LocalDateTime;

public interface TicketReaderInfo {

    Station station();

    LocalDateTime time();

    UsageType usageType();

}
