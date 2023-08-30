package de.sotterbeck.iumetro.entity.ticket;

public interface TicketReader extends TicketReaderInfo {

    void tap(Ticket ticket);

    boolean shouldOpenGate(Ticket ticket);

    boolean shouldFineUser(Ticket ticket);

}
