package de.sotterbeck.iumetro.entity.ticket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TicketUsageLimitConstraintTest {

    private TicketFactory ticketFactory;

    @BeforeEach
    void setUp() {
        ticketFactory = new SimpleTicketFactory();
    }

    @Test
    void isValid_ShouldBeTrue_WhenUsageLimitNotSurpassed() {
        int limit = 1;

        Ticket ticket = ticketFactory.createConstrainedTicket("Single-use Ticket", UUID.randomUUID())
                .usageLimit(limit)
                .build();
        boolean valid = ticket.isValid();

        assertThat(valid).isTrue();
    }

    @Test
    void isValid_ShouldBeFalse_WhenUsageLimitSurpassedBeforeEntry() {
        int limit = 1;
        TicketReaderInfo oneUsage = createEntryReader();

        Ticket ticket = ticketFactory.createConstrainedTicket("Single-use Ticket", UUID.randomUUID())
                .usageLimit(limit)
                .addUsage(oneUsage)
                .build();
        boolean valid = ticket.isValid();

        assertThat(valid).isFalse();
    }

    @Test
    void isValid_ShouldBeFalse_WhenExitAfterLimitNotSurpassedAtEntry() {
        int limit = 1;
        TicketReaderInfo oneUsage = createEntryReader();
        TicketReaderInfo oneUsageExit = createExitReader();

        Ticket ticket = ticketFactory.createConstrainedTicket("Single-use Ticket", UUID.randomUUID())
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
        TicketReaderInfo firstUsage = createEntryReader();
        TicketReaderInfo firstUsageExit = createExitReader();

        Ticket ticket = ticketFactory.createConstrainedTicket("Single-use Ticket", UUID.randomUUID())
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

        Ticket ticket = ticketFactory.createConstrainedTicket("Single-use Ticket", UUID.randomUUID())
                .usageLimit(limit)
                .build();
        boolean valid = ticket.isValid();

        assertThat(valid).isTrue();
    }

    private TicketReaderInfo createEntryReader() {
        return new TicketEntryReaderFactory().create(new SimpleStation("any"));
    }

    private TicketReader createExitReader() {
        return new TicketExitReaderFactory().create(new SimpleStation("any"));
    }

}