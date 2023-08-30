package de.sotterbeck.iumetro.entity.ticket;

public interface RechargeableTicketBuilder {

    RechargeableTicketBuilder addUsage(TicketReaderInfo ticketReader);

    RechargeableTicketBuilder paidUsageLimit(int paidUsages);

    RechargeableTicket build();

}
