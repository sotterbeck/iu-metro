package de.sotterbeck.iumetro.entity.ticket;

import java.time.LocalDateTime;

class TicketEntryReader implements TicketReader {

    private final Station station;

    TicketEntryReader(Station station) {
        this.station = station;
    }

    @Override
    public void tap(Ticket ticket) {
        if (shouldOpenGate(ticket)) {
            ticket.onEntry(new ImmutableTicketReaderInfo(station, LocalDateTime.now(), usageType()));
        }
    }

    @Override
    public boolean shouldOpenGate(Ticket ticket) {
        if (ticket.isInSystem()) {
            return false;
        }
        return ticket.isValid();
    }

    @Override
    public boolean shouldFineUser(Ticket ticket) {
        return false;
    }

    @Override
    public Station station() {
        return station;
    }

    @Override
    public LocalDateTime time() {
        return LocalDateTime.now();
    }

    @Override
    public UsageType usageType() {
        return UsageType.ENTRY;
    }

}
