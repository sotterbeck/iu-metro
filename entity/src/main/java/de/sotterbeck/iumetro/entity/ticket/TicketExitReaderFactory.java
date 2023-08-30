package de.sotterbeck.iumetro.entity.ticket;

public class TicketExitReaderFactory implements TicketReaderFactory {

    public TicketReader create(Station station) {
        return new TicketExitReader(station);
    }

}
