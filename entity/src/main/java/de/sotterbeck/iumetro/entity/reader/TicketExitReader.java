package de.sotterbeck.iumetro.entity.reader;

import de.sotterbeck.iumetro.entity.station.Station;
import de.sotterbeck.iumetro.entity.ticket.ImmutableTicketReaderInfo;
import de.sotterbeck.iumetro.entity.ticket.Ticket;
import de.sotterbeck.iumetro.entity.ticket.UsageType;

import java.time.LocalDateTime;

class TicketExitReader implements TicketReader {

    private final Station station;

    TicketExitReader(Station station) {
        this.station = station;
    }

    @Override
    public void tap(Ticket ticket) {
        ticket.onExit(new ImmutableTicketReaderInfo(station(), LocalDateTime.now(), usageType()));
    }

    @Override
    public boolean shouldOpenGate(Ticket ticket) {
        return true;
    }

    @Override
    public boolean shouldFineUser(Ticket ticket) {
        return !ticket.isInSystem();
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
        return UsageType.EXIT;
    }

}
