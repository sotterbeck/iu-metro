package de.sotterbeck.iumetro.entity.ticket;

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
