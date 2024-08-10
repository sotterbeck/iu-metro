package de.sotterbeck.iumetro.entity.ticket.constained;

import de.sotterbeck.iumetro.entity.ticket.Ticket;

public interface TicketUsageConstraint {

    boolean isValid(Ticket ticket);

}
