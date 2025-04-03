package de.sotterbeck.iumetro.domain.ticket.constained;

import de.sotterbeck.iumetro.domain.reader.TicketReaderInfo;
import de.sotterbeck.iumetro.domain.ticket.Ticket;
import de.sotterbeck.iumetro.domain.ticket.UsageType;

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
