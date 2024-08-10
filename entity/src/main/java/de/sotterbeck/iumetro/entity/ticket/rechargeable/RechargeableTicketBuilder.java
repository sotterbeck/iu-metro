package de.sotterbeck.iumetro.entity.ticket.rechargeable;

import de.sotterbeck.iumetro.entity.reader.TicketReaderInfo;

public interface RechargeableTicketBuilder {

    RechargeableTicketBuilder addUsage(TicketReaderInfo ticketReader);

    RechargeableTicketBuilder paidUsageLimit(int paidUsages);

    RechargeableTicket build();

}
