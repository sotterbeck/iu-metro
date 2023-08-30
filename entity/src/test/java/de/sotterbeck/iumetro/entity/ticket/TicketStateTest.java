package de.sotterbeck.iumetro.entity.ticket;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TicketStateTest {

    @Test
    void isInSystem_ShouldBeFalse_WhenTicketIsInvalid() {
        TicketReader reader = new TicketEntryReader(new SimpleStation("any"));
        Ticket ticket = Tickets.createConstrainedTicket("Common Ticket", UUID.randomUUID())
                .customLimit(t -> false)
                .build();

        reader.tap(ticket);
        boolean inSystem = ticket.isInSystem();

        assertThat(inSystem).isFalse();
    }

    @Test
    void isInSystem_ShouldBeTrue_WhenTicketIsValid() {
        TicketReader reader = new TicketEntryReader(new SimpleStation("any"));
        Ticket ticket = Tickets.createConstrainedTicket("Common Ticket", UUID.randomUUID()).build();

        reader.tap(ticket);
        boolean inSystem = ticket.isInSystem();

        assertThat(inSystem).isTrue();
    }

    @Test
    void isInSystem_ShouldBeTrue_WhenLastValidUsageWasAEntryGate() {
        TicketReaderInfo entryReader = new TicketEntryReader(new SimpleStation("any"));

        Ticket ticket = Tickets.createConstrainedTicket("Common Ticket", UUID.randomUUID())
                .addUsage(entryReader)
                .build();
        boolean inSystem = ticket.isInSystem();

        assertThat(inSystem).isTrue();

    }

    @Test
    void isInSystem_ShouldBeFalse_WhenLastValidUsageWasAExitGate() {
        TicketReaderInfo exitReader = new TicketExitReader(new SimpleStation("any"), LocalDateTime.now());

        Ticket ticket = Tickets.createConstrainedTicket("Common Ticket", UUID.randomUUID())
                .addUsage(exitReader)
                .build();
        boolean inSystem = ticket.isInSystem();

        assertThat(inSystem).isFalse();

    }

}