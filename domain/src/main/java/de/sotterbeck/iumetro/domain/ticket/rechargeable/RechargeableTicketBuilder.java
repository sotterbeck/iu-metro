package de.sotterbeck.iumetro.domain.ticket.rechargeable;

import de.sotterbeck.iumetro.domain.reader.TicketReaderInfo;

public interface RechargeableTicketBuilder {

    RechargeableTicketBuilder addUsage(TicketReaderInfo ticketReader);

    RechargeableTicketBuilder paidUsageLimit(int paidUsages);

    RechargeableTicket build();

}
