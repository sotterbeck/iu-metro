package de.sotterbeck.iumetro.entity.ticket;

public interface TicketReader extends TicketReaderInfo {

    void tap(Ticket ticket);

    boolean opensGate(Ticket ticket);

    default boolean finesUser(Ticket ticket) {
        return false;
    }

}
