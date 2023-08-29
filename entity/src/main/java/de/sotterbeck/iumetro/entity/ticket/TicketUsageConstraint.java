package de.sotterbeck.iumetro.entity.ticket;

public interface TicketUsageConstraint {

    boolean isValid(Ticket ticket);

}
