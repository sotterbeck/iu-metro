package de.sotterbeck.iumetro.domain.ticket;

import de.sotterbeck.iumetro.domain.reader.TicketReaderInfo;
import de.sotterbeck.iumetro.domain.station.Station;

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
