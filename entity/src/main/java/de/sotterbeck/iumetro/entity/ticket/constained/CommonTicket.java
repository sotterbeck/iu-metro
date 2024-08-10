package de.sotterbeck.iumetro.entity.ticket.constained;

import de.sotterbeck.iumetro.entity.reader.TicketReaderInfo;
import de.sotterbeck.iumetro.entity.ticket.AbstractTicket;

import java.util.List;
import java.util.UUID;

public class CommonTicket extends AbstractTicket {

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
