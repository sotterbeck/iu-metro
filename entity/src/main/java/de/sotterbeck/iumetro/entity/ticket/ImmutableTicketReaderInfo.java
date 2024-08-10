package de.sotterbeck.iumetro.entity.ticket;

import de.sotterbeck.iumetro.entity.reader.TicketReaderInfo;
import de.sotterbeck.iumetro.entity.station.Station;

import java.time.LocalDateTime;

public record ImmutableTicketReaderInfo(
        Station station,
        LocalDateTime time,
        UsageType usageType
) implements TicketReaderInfo {

    static TicketReaderInfo ofReader(TicketReaderInfo ticketReader) {
        return new ImmutableTicketReaderInfo(ticketReader.station(), ticketReader.time(), ticketReader.usageType());
    }

}
