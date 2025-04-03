package de.sotterbeck.iumetro.domain.ticket;

import de.sotterbeck.iumetro.domain.reader.TicketReader;
import de.sotterbeck.iumetro.domain.reader.TicketReaderFactory;
import de.sotterbeck.iumetro.domain.reader.TicketReaderFactoryImpl;
import de.sotterbeck.iumetro.domain.reader.TicketReaderInfo;
import de.sotterbeck.iumetro.domain.station.SimpleStation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

class TicketEntryReaderTest {

    private TicketReader underTest;
    private TicketFactory ticketFactory;

    @BeforeEach
    void setUp() {
        underTest = new TicketReaderFactoryImpl().create(TicketReaderFactory.ReaderType.ENTRY, new SimpleStation("any"));
        ticketFactory = new SimpleTicketFactory();
    }

    @Nested
    class OpensGate {

        @Test
        void shouldReturnFalse_WhenTicketIsNotValid() {
            TicketReader reader = underTest;

            Ticket ticket = ticketFactory.createConstrainedTicket("Invalid Ticket", UUID.randomUUID())
                    .customLimit(Invalid())
                    .build();
            boolean shouldOpen = reader.shouldOpenGate(ticket);

            assertThat(shouldOpen).isFalse();
        }

        @Test
        void shouldReturnTrue_WhenTicketIsValid() {
            TicketReader reader = underTest;

            Ticket ticket = ticketFactory.createConstrainedTicket("Valid Ticket", UUID.randomUUID()).build();
            boolean shouldOpen = reader.shouldOpenGate(ticket);

            assertThat(shouldOpen).isTrue();
        }

        @Test
        void shouldBeFalse_WhenTicketIsInSystem() {
            TicketReader reader = underTest;
            TicketReaderInfo entry = underTest;

            Ticket ticket = ticketFactory.createConstrainedTicket("Simple Ticket", UUID.randomUUID())
                    .addUsage(entry)
                    .build();
            boolean shouldOpen = reader.shouldOpenGate(ticket);

            assertThat(shouldOpen).isFalse();
        }

    }

    @Nested
    class Tap {

        @Test
        void tap_ShouldAddUsage_WhenTicketIsValid() {
            TicketReader reader = underTest;

            Ticket ticket = ticketFactory.createConstrainedTicket("Valid Ticket", UUID.randomUUID())
                    .build();
            reader.tap(ticket);
            int usages = ticket.usageCount();

            assertThat(usages).isEqualTo(1);
        }

        @Test
        void tap_ShouldNotAddUsage_WhenTicketIsInvalid() {
            TicketReader reader = underTest;

            Ticket ticket = ticketFactory.createConstrainedTicket("Invalid Ticket", UUID.randomUUID())
                    .customLimit(Invalid())
                    .build();
            reader.tap(ticket);
            int usages = ticket.usageCount();

            assertThat(usages).isZero();

        }

        @Test
        void tap_ShouldNotAddUsage_WhenTicketIsInSystem() {
            TicketReader reader = underTest;

            Ticket ticket = ticketFactory.createConstrainedTicket("Simple Ticket", UUID.randomUUID())
                    .addUsage(underTest)
                    .build();
            reader.tap(ticket);

            assertThat(ticket.usageCount()).isEqualTo(1);
        }

    }

    private static Predicate<Ticket> Invalid() {
        return t -> false;
    }

}