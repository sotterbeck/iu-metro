package de.sotterbeck.iumetro.domain.ticket.constained;

import de.sotterbeck.iumetro.domain.ticket.Ticket;

public interface TicketUsageConstraint {

    boolean isValid(Ticket ticket);

}
