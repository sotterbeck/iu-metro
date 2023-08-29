package de.sotterbeck.iumetro.entity.ticket;

class NoUsageConstraint implements TicketUsageConstraint {

    @Override
    public boolean isValid(Ticket ticket) {
        return true;
    }

}
