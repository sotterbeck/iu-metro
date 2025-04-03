package de.sotterbeck.iumetro.domain.ticket;

import de.sotterbeck.iumetro.domain.ticket.constained.ConstrainedTicketBuilder;
import de.sotterbeck.iumetro.domain.ticket.rechargeable.RechargeableTicketBuilder;

import java.math.BigDecimal;
import java.util.UUID;

public interface TicketFactory {

    RechargeableTicketBuilder createRechargeableTicket(String name, UUID id, BigDecimal balance, BigDecimal usageCost);

    ConstrainedTicketBuilder createConstrainedTicket(String name, UUID id);

}
