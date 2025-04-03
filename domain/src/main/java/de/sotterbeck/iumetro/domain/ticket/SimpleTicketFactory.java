package de.sotterbeck.iumetro.domain.ticket;

import de.sotterbeck.iumetro.domain.ticket.constained.ConstrainedTicketBuilder;
import de.sotterbeck.iumetro.domain.ticket.rechargeable.CommonRechargeableTicketBuilder;
import de.sotterbeck.iumetro.domain.ticket.rechargeable.RechargeableTicketBuilder;

import java.math.BigDecimal;
import java.util.UUID;

public final class SimpleTicketFactory implements TicketFactory {

    @Override
    public RechargeableTicketBuilder createRechargeableTicket(String name, UUID id, BigDecimal balance, BigDecimal usageCost) {
        return new CommonRechargeableTicketBuilder(name, id, balance, usageCost);
    }

    @Override
    public ConstrainedTicketBuilder createConstrainedTicket(String name, UUID id) {
        return new CommonTicketBuilder(name, id);
    }

}
