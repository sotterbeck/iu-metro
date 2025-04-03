package de.sotterbeck.iumetro.domain.reader;

import de.sotterbeck.iumetro.domain.ticket.Ticket;

public interface TicketReader extends TicketReaderInfo {

    void tap(Ticket ticket);

    boolean shouldOpenGate(Ticket ticket);

    boolean shouldFineUser(Ticket ticket);

}
