package de.sotterbeck.iumetro.entity.ticket;

import java.time.LocalDateTime;

public class TicketEntryReader implements TicketReader {

    private final Station station;
    private final LocalDateTime time;

    public TicketEntryReader(Station station) {
        this(station, LocalDateTime.now());
    }

    public TicketEntryReader(Station station, LocalDateTime time) {
        this.station = station;
        this.time = time;
    }

    @Override
    public void tap(Ticket ticket) {
        if (opensGate(ticket)) {
            ticket.onEntry(new ImmutableTicketReaderInfo(station, time, usageType()));
        }
    }

    @Override
    public boolean opensGate(Ticket ticket) {
        if (ticket.isInSystem()) {
            return false;
        }
        return ticket.isValid();
    }

    @Override
    public Station station() {
        return station;
    }

    @Override
    public LocalDateTime time() {
        return time;
    }

    @Override
    public UsageType usageType() {
        return UsageType.ENTRY;
    }

}
