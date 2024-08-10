package de.sotterbeck.iumetro.entity.ticket.rechargeable;

import de.sotterbeck.iumetro.entity.ticket.Ticket;

import java.math.BigDecimal;

public interface RechargeableTicket extends Ticket {

    BigDecimal balance();

    void addBalance(BigDecimal amount);

    void subtractBalance(BigDecimal amount);

}
