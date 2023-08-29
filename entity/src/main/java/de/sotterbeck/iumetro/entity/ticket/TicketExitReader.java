package de.sotterbeck.iumetro.entity.ticket;

import java.time.LocalDateTime;

public class TicketExitReader implements TicketReader {

    private final Station station;
    private final LocalDateTime time;

    public TicketExitReader(Station station, LocalDateTime time) {
        this.station = station;
        this.time = time;
    }

    public TicketExitReader(Station station) {
        this(station, LocalDateTime.now());
    }

    @Override
    public void tap(Ticket ticket) {
        ticket.onExit(new ImmutableTicketReaderInfo(station(), time, usageType()));
    }

    @Override
    public boolean opensGate(Ticket ticket) {
        return true;
    }

    @Override
    public boolean finesUser(Ticket ticket) {
        return !ticket.isInSystem();
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
        return UsageType.EXIT;
    }

}
