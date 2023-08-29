package de.sotterbeck.iumetro.entity.ticket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

class TicketExitReaderTest {

    private TicketExitReader underTest;

    @BeforeEach
    void setUp() {
        underTest = new TicketExitReader(new SimpleStation("any"));
    }

    @Nested
    class Tap {

        @Test
        void shouldAddUsage_WhenTicketIsValid() {
            TicketReader reader = underTest;

            Ticket ticket = new CommonTicketBuilder("Valid Ticket", UUID.randomUUID())
                    .build();
            reader.tap(ticket);

            assertThat(ticket.usageCount()).isEqualTo(1);
        }

        @Test
        void shouldAddUsage_WhenTicketIsNotValid() {
            TicketReader reader = underTest;

            Ticket ticket = new CommonTicketBuilder("Valid Ticket", UUID.randomUUID())
                    .customLimit(invalid())
                    .build();
            reader.tap(ticket);
            int usageCount = ticket.usageCount();

            assertThat(usageCount).isEqualTo(1);
        }

    }

    @Nested
    class OpensGate {

        @Test
        void shouldReturnTrue_WhenTicketIsValid() {
            TicketReader reader = underTest;

            Ticket ticket = new CommonTicketBuilder("Valid Ticket", UUID.randomUUID())
                    .build();
            boolean shouldOpenGate = reader.opensGate(ticket);

            assertThat(shouldOpenGate).isTrue();
        }

        @Test
        void shouldReturnTrue_WhenTicketIsNotValid() {
            TicketReader reader = underTest;

            Ticket ticket = new CommonTicketBuilder("Valid Ticket", UUID.randomUUID())
                    .customLimit(invalid())
                    .build();
            boolean shouldOpenGate = reader.opensGate(ticket);

            assertThat(shouldOpenGate).isTrue();
        }

    }

    @Test
    void finesUser_ShouldReturnFalse_WhenTicketLastUsageWasAtEntryReader() {
        TicketReader exitReader = underTest;
        TicketReaderInfo entryReader = new TicketEntryReader(new SimpleStation("any"));

        Ticket ticket = new CommonTicketBuilder("Common Ticket", UUID.randomUUID())
                .addUsage(entryReader)
                .build();
        boolean finesUser = exitReader.finesUser(ticket);

        assertThat(finesUser).isFalse();
    }

    @Test
    void finesUser_ShouldReturnTrue_WhenTicketLastUsageWasNotAtEntryReader() {
        TicketReader exitReader = underTest;

        Ticket ticket = new CommonTicketBuilder("Common Ticket", UUID.randomUUID())
                .build();
        boolean finesUser = exitReader.finesUser(ticket);

        assertThat(finesUser).isTrue();
    }

    private static Predicate<Ticket> invalid() {
        return t -> false;
    }

}