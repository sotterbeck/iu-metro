package de.sotterbeck.iumetro.entity.ticket.constained;

import de.sotterbeck.iumetro.entity.reader.TicketReaderInfo;
import de.sotterbeck.iumetro.entity.ticket.Ticket;
import de.sotterbeck.iumetro.entity.ticket.UsageType;

public class UsageLimitConstraint implements TicketUsageConstraint {

    private final int limit;

    public UsageLimitConstraint(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean isValid(Ticket ticket) {
        return countOfEntriesFor(ticket) < limit;
    }

    private long countOfEntriesFor(Ticket ticket) {
        return ticket.usages().stream()
                .map(TicketReaderInfo::usageType)
                .filter(usageType -> UsageType.ENTRY == usageType)
                .count();
    }

}
