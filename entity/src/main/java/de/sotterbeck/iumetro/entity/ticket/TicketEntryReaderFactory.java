package de.sotterbeck.iumetro.entity.ticket;

public class TicketEntryReaderFactory implements TicketReaderFactory {

    @Override
    public TicketReader create(Station station) {
        return new TicketEntryReader(station);
    }

}
