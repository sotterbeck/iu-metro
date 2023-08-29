package de.sotterbeck.iumetro.entity.ticket;

import java.util.List;
import java.util.UUID;

class CommonTicket extends AbstractTicket {

    private final TicketUsageConstraint ticketValidator;

    public CommonTicket(String name, UUID id, TicketUsageConstraint ticketValidator, List<TicketReaderInfo> usages) {
        super(name, id, usages);
        this.ticketValidator = ticketValidator;
    }

    @Override
    public boolean isValid() {
        return ticketValidator.isValid(this);
    }

}
