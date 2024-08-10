package de.sotterbeck.iumetro.entity.ticket;

import de.sotterbeck.iumetro.entity.ticket.constained.ConstrainedTicketBuilder;
import de.sotterbeck.iumetro.entity.ticket.rechargeable.RechargeableTicketBuilder;

import java.math.BigDecimal;
import java.util.UUID;

public interface TicketFactory {

    RechargeableTicketBuilder createRechargeableTicket(String name, UUID id, BigDecimal balance, BigDecimal usageCost);

    ConstrainedTicketBuilder createConstrainedTicket(String name, UUID id);

}
