package de.sotterbeck.iumetro.entity.ticket;

import de.sotterbeck.iumetro.entity.reader.TicketReader;
import de.sotterbeck.iumetro.entity.reader.TicketReaderFactory;
import de.sotterbeck.iumetro.entity.reader.TicketReaderFactoryImpl;
import de.sotterbeck.iumetro.entity.reader.TicketReaderInfo;
import de.sotterbeck.iumetro.entity.station.SimpleStation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TicketInSystemStateTest {

    private final TicketReaderFactory ticketReaderFactory = new TicketReaderFactoryImpl();
    private TicketFactory ticketFactory;

    @BeforeEach
    void setUp() {
        ticketFactory = new SimpleTicketFactory();
    }

    @Test
    void isInSystem_ShouldBeFalse_WhenTicketIsInvalid() {
        TicketReader reader = createEntryReader();
        Ticket ticket = ticketFactory.createConstrainedTicket("Common Ticket", UUID.randomUUID())
                .customLimit(t -> false)
                .build();

        reader.tap(ticket);
        boolean inSystem = ticket.isInSystem();

        assertThat(inSystem).isFalse();
    }

    @Test
    void isInSystem_ShouldBeTrue_WhenTicketIsValid() {
        TicketReader reader = createEntryReader();
        Ticket ticket = ticketFactory.createConstrainedTicket("Common Ticket", UUID.randomUUID()).build();

        reader.tap(ticket);
        boolean inSystem = ticket.isInSystem();

        assertThat(inSystem).isTrue();
    }

    @Test
    void isInSystem_ShouldBeTrue_WhenLastValidUsageWasAEntryGate() {
        TicketReaderInfo entryReader = createEntryReader();

        Ticket ticket = ticketFactory.createConstrainedTicket("Common Ticket", UUID.randomUUID())
                .addUsage(entryReader)
                .build();
        boolean inSystem = ticket.isInSystem();

        assertThat(inSystem).isTrue();

    }

    @Test
    void isInSystem_ShouldBeFalse_WhenLastValidUsageWasAExitGate() {
        TicketReaderInfo exitReader = createExitReader();

        Ticket ticket = ticketFactory.createConstrainedTicket("Common Ticket", UUID.randomUUID())
                .addUsage(exitReader)
                .build();
        boolean inSystem = ticket.isInSystem();

        assertThat(inSystem).isFalse();

    }

    private TicketReader createEntryReader() {
        return ticketReaderFactory.create(TicketReaderFactory.ReaderType.ENTRY, new SimpleStation("any"));
    }

    private TicketReader createExitReader() {
        return ticketReaderFactory.create(TicketReaderFactory.ReaderType.EXIT, new SimpleStation("any"));
    }

}