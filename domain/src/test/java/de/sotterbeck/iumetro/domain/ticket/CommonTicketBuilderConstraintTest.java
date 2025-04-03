package de.sotterbeck.iumetro.domain.ticket;

import de.sotterbeck.iumetro.domain.reader.TicketReader;
import de.sotterbeck.iumetro.domain.reader.TicketReaderFactory;
import de.sotterbeck.iumetro.domain.reader.TicketReaderFactoryImpl;
import de.sotterbeck.iumetro.domain.reader.TicketReaderInfo;
import de.sotterbeck.iumetro.domain.station.SimpleStation;
import de.sotterbeck.iumetro.domain.ticket.constained.ConstrainedTicketBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CommonTicketBuilderConstraintTest {

    private final TicketReaderFactory ticketReaderFactory = new TicketReaderFactoryImpl();
    private ConstrainedTicketBuilder underTest;

    @BeforeEach
    void setUp() {
        TicketFactory ticketFactory = new SimpleTicketFactory();
        underTest = ticketFactory.createConstrainedTicket("Ticket", UUID.randomUUID());
    }

    @Test
    void isValid_ShouldReturnTrue_WhenNoLimitIsAdded() {
        Ticket ticket = underTest.build();

        boolean valid = ticket.isValid();

        assertThat(valid).isTrue();
    }

    @Test
    void isValid_ShouldReturnTrue_WhenTicketBothUsageConstraintsReturnValid() {
        Ticket ticket = underTest
                .usageLimit(10)
                .timeLimit(Duration.ofDays(31), LocalDateTime.now())
                .build();

        boolean valid = ticket.isValid();

        assertThat(valid).isTrue();
    }

    @Test
    void isValid_ShouldReturnFalse_WhenUsageLimitIsNotValidAndTimeLimitIsValid() {
        int limit = 1;
        TicketReaderInfo entryReader = createEntryReader();
        TicketReaderInfo exitReader = createExitReader();

        Ticket ticket = underTest
                .usageLimit(limit)
                .timeLimit(Duration.ofDays(31), LocalDateTime.now())
                .addUsage(entryReader)
                .addUsage(exitReader)
                .addUsage(entryReader)
                .build();
        boolean valid = ticket.isValid();

        assertThat(valid).isFalse();
    }

    @Test
    void isValid_ShouldReturnFalse_WhenUsageLimitIsValidAndTimeLimitIsNotValid() {
        TicketReaderInfo entryReader = createEntryReader();
        TicketReaderInfo exitReader = createExitReader();
        Duration timeLimit = Duration.ofDays(31);

        LocalDateTime timeAtTestAfterTimeSurpassed = LocalDateTime.now().plusDays(90);
        Ticket ticket = underTest
                .usageLimit(2)
                .timeLimit(timeLimit, timeAtTestAfterTimeSurpassed)
                .addUsage(entryReader)
                .addUsage(exitReader)
                .addUsage(entryReader)
                .build();
        boolean valid = ticket.isValid();

        assertThat(valid).isFalse();
    }

    private TicketReader createExitReader() {
        return ticketReaderFactory.create(TicketReaderFactory.ReaderType.EXIT, new SimpleStation("any"));
    }

    private TicketReader createEntryReader() {
        return ticketReaderFactory.create(TicketReaderFactory.ReaderType.ENTRY, new SimpleStation("any"));
    }

}