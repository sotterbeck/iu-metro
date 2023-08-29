package de.sotterbeck.iumetro.entity.ticket;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class CommonTicketBuilder {

    private final String name;
    private final UUID id;
    private final List<TicketReaderInfo> usageList = new ArrayList<>();
    private TicketUsageConstraintComposite usageConstraints = new TicketUsageConstraintComposite();

    public CommonTicketBuilder(String name, UUID id) {
        this.name = name;
        this.id = id;
    }

    public CommonTicketBuilder timeLimit(Duration timeLimit, LocalDateTime timeAtTest) {
        if (!timeLimit.isZero()) {
            usageConstraints.add(new TimeLimitConstraint(timeLimit, timeAtTest));
        }
        return this;

    }

    public CommonTicketBuilder usageLimit(int limit) {
        if (limit != 0) {
            usageConstraints.add(new UsageLimitConstraint(limit));
        }
        return this;
    }

    public CommonTicketBuilder customLimit(Predicate<Ticket> validationTest) {
        usageConstraints.add(CustomUsageConstraint.ofTest(validationTest));
        return this;
    }

    public CommonTicketBuilder addUsage(TicketReaderInfo usage) {
        usageList.add(usage);
        return this;
    }

    public Ticket build() {
        return new CommonTicket(name, id, usageConstraints, usageList);
    }

    private static class TicketUsageConstraintComposite implements TicketUsageConstraint {

        private final List<TicketUsageConstraint> ticketConstraints = new ArrayList<>();

        @Override
        public boolean isValid(Ticket ticket) {
            return ticketConstraints.stream()
                    .allMatch(usageConstraint -> usageConstraint.isValid(ticket));
        }

        public void add(TicketUsageConstraint usageConstraint) {
            ticketConstraints.add(usageConstraint);
        }

    }

}