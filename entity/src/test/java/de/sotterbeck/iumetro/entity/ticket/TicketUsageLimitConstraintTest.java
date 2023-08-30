package de.sotterbeck.iumetro.entity.ticket;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TicketUsageLimitConstraintTest {

    @Test
    void isValid_ShouldBeTrue_WhenUsageLimitNotSurpassed() {
        int limit = 1;

        Ticket ticket = Tickets.createConstrainedTicket("Single-use Ticket", UUID.randomUUID())
                .usageLimit(limit)
                .build();
        boolean valid = ticket.isValid();

        assertThat(valid).isTrue();
    }

    @Test
    void isValid_ShouldBeFalse_WhenUsageLimitSurpassedBeforeEntry() {
        int limit = 1;
        TicketReaderInfo oneUsage = createEntryTicketReader();

        Ticket ticket = Tickets.createConstrainedTicket("Single-use Ticket", UUID.randomUUID())
                .usageLimit(limit)
                .addUsage(oneUsage)
                .build();
        boolean valid = ticket.isValid();

        assertThat(valid).isFalse();
    }

    @Test
    void isValid_ShouldBeFalse_WhenExitAfterLimitNotSurpassedAtEntry() {
        int limit = 1;
        TicketReaderInfo oneUsage = createEntryTicketReader();
        TicketReaderInfo oneUsageExit = new TicketExitReader(new SimpleStation("any"));

        Ticket ticket = Tickets.createConstrainedTicket("Single-use Ticket", UUID.randomUUID())
                .usageLimit(limit)
                .addUsage(oneUsage)
                .addUsage(oneUsageExit)
                .build();
        boolean valid = ticket.isValid();

        assertThat(valid).isFalse();
    }

    @Test
    void isValid_ShouldBeTrue_WhenLimitNotSurpassedAtSecondEntry() {
        int limit = 2;
        TicketReaderInfo firstUsage = createEntryTicketReader();
        TicketReaderInfo firstUsageExit = new TicketExitReader(new SimpleStation("any"));

        Ticket ticket = Tickets.createConstrainedTicket("Single-use Ticket", UUID.randomUUID())
                .usageLimit(limit)
                .addUsage(firstUsage)
                .addUsage(firstUsageExit)
                .build();
        boolean valid = ticket.isValid();

        assertThat(valid).isTrue();
    }

    @Test
    void isValid_ShouldBeTrue_WhenUsageLimitIsZero() {
        int limit = 0;

        Ticket ticket = Tickets.createConstrainedTicket("Single-use Ticket", UUID.randomUUID())
                .usageLimit(limit)
                .build();
        boolean valid = ticket.isValid();

        assertThat(valid).isTrue();
    }

    private TicketReaderInfo createEntryTicketReader() {
        return new TicketEntryReader(new SimpleStation("any"));
    }

}