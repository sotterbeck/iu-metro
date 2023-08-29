package de.sotterbeck.iumetro.entity.ticket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

class TicketEntryReaderTest {

    private TicketEntryReader underTest;

    @BeforeEach
    void setUp() {
        underTest = new TicketEntryReader(anyStation());
    }

    @Nested
    class OpensGate {

        @Test
        void shouldReturnFalse_WhenTicketIsNotValid() {
            TicketReader reader = underTest;

            Ticket ticket = new CommonTicketBuilder("Invalid Ticket", UUID.randomUUID())
                    .customLimit(Invalid())
                    .build();
            boolean shouldOpen = reader.opensGate(ticket);

            assertThat(shouldOpen).isFalse();
        }

        @Test
        void shouldReturnTrue_WhenTicketIsValid() {
            TicketReader reader = underTest;

            Ticket ticket = new CommonTicketBuilder("Valid Ticket", UUID.randomUUID()).build();
            boolean shouldOpen = reader.opensGate(ticket);

            assertThat(shouldOpen).isTrue();
        }

        @Test
        void shouldBeFalse_WhenTicketIsInSystem() {
            TicketReader reader = underTest;
            TicketReaderInfo entry = underTest;

            Ticket ticket = new CommonTicketBuilder("Simple Ticket", UUID.randomUUID())
                    .addUsage(entry)
                    .build();
            boolean shouldOpen = reader.opensGate(ticket);

            assertThat(shouldOpen).isFalse();
        }

    }

    @Nested
    class Tap {

        @Test
        void tap_ShouldAddUsage_WhenTicketIsValid() {
            TicketReader reader = underTest;

            Ticket ticket = new CommonTicketBuilder("Valid Ticket", UUID.randomUUID())
                    .build();
            reader.tap(ticket);
            int usages = ticket.usageCount();

            assertThat(usages).isEqualTo(1);
        }

        @Test
        void tap_ShouldNotAddUsage_WhenTicketIsInvalid() {
            TicketReader reader = underTest;

            Ticket ticket = new CommonTicketBuilder("Invalid Ticket", UUID.randomUUID())
                    .customLimit(Invalid())
                    .build();
            reader.tap(ticket);
            int usages = ticket.usageCount();

            assertThat(usages).isZero();

        }

        @Test
        void tap_ShouldNotAddUsage_WhenTicketIsInSystem() {
            TicketReader reader = underTest;

            Ticket ticket = new CommonTicketBuilder("Simple Ticket", UUID.randomUUID())
                    .addUsage(underTest)
                    .build();
            reader.tap(ticket);

            assertThat(ticket.usageCount()).isEqualTo(1);
        }

    }

    private static Predicate<Ticket> Invalid() {
        return t -> false;
    }

    private static SimpleStation anyStation() {
        return new SimpleStation("any");
    }

}