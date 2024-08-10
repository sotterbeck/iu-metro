package de.sotterbeck.iumetro.entity.reader;

import de.sotterbeck.iumetro.entity.ticket.Ticket;

public interface TicketReader extends TicketReaderInfo {

    void tap(Ticket ticket);

    boolean shouldOpenGate(Ticket ticket);

    boolean shouldFineUser(Ticket ticket);

}
