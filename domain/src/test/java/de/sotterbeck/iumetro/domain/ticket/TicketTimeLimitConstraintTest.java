package de.sotterbeck.iumetro.domain.ticket;

import de.sotterbeck.iumetro.domain.reader.TicketReaderFactory;
import de.sotterbeck.iumetro.domain.reader.TicketReaderFactoryImpl;
import de.sotterbeck.iumetro.domain.reader.TicketReaderInfo;
import de.sotterbeck.iumetro.domain.station.SimpleStation;
import de.sotterbeck.iumetro.domain.ticket.constained.TimeLimitConstraint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;

class TicketTimeLimitConstraintTest {

    private TicketFactory ticketFactory;

    @BeforeEach
    void setUp() {
        ticketFactory = new SimpleTicketFactory();
    }

    @Test
    void constructor_ShouldThrowException_WhenTimeLimitIsNegative() {
        Duration timeLimit = Duration.ofHours(-1);

        Throwable thrown = catchThrowable(() -> new TimeLimitConstraint(timeLimit, LocalDateTime.now()));

        then(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void isValid_ShouldBeFalse_WhenTimeLimitNotSurpassedBeforeEntry() {
        Duration timeLimit = Duration.ofHours(4);
        TicketReaderInfo firstUsage = createEntryReader();

        LocalDateTime timeAtTestNotSurpassed = firstUsage.time().plusHours(1);
        Ticket ticket = ticketFactory.createConstrainedTicket("4-Hour Ticket", UUID.randomUUID())
                .timeLimit(timeLimit, timeAtTestNotSurpassed)
                .addUsage(firstUsage)
                .build();
        boolean valid = ticket.isValid();

        assertThat(valid).isTrue();
    }

    @Test
    void isValid_ShouldBeTrue_WhenTimeLimitSurpassedBeforeEntry() {
        Duration timeLimit = Duration.ofHours(4);
        TicketReaderInfo firstUsage = createEntryReader();

        LocalDateTime timeAtTestSurpassed = firstUsage.time().plusHours(5);
        Ticket ticket = ticketFactory.createConstrainedTicket("4-Hour Ticket", UUID.randomUUID())
                .timeLimit(timeLimit, timeAtTestSurpassed)
                .addUsage(firstUsage)
                .build();
        boolean valid = ticket.isValid();

        assertThat(valid).isFalse();
    }

    @Test
    void isValid_ShouldBeTrue_WhenTicketWithTimeConstantHasNoUsage() {
        Duration timeLimit = Duration.ofHours(4);
        LocalDateTime timeAtTest = LocalDateTime.now();
        Ticket ticket = ticketFactory.createConstrainedTicket("4-Hour Ticket", UUID.randomUUID())
                .timeLimit(timeLimit, timeAtTest)
                .build();

        boolean valid = ticket.isValid();

        assertThat(valid).isTrue();
    }

    @Test
    void isValid_ShouldBeTrue_WhenTimeLimitIsZero() {
        Duration timeLimit = Duration.ofHours(0);
        LocalDateTime timeAtTest = LocalDateTime.now().plusSeconds(1);
        TicketReaderInfo firstUsage = createEntryReader();
        Ticket ticket = ticketFactory.createConstrainedTicket("Valid Ticket", UUID.randomUUID())
                .timeLimit(timeLimit, timeAtTest)
                .addUsage(firstUsage)
                .build();

        boolean valid = ticket.isValid();

        assertThat(valid).isTrue();
    }

    private TicketReaderInfo createEntryReader() {
        return new TicketReaderFactoryImpl().create(TicketReaderFactory.ReaderType.ENTRY, new SimpleStation("any"));
    }

}