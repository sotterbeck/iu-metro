package de.sotterbeck.iumetro.entity.ticket;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;

class TicketTimeLimitConstraintTest {

    @Test
    void constructor_ShouldThrowException_WhenTimeLimitIsNegative() {
        Duration timeLimit = Duration.ofHours(-1);

        Throwable thrown = catchThrowable(() -> new TimeLimitConstraint(timeLimit, LocalDateTime.now()));

        then(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void isValid_ShouldBeFalse_WhenTimeLimitNotSurpassedBeforeEntry() {
        Duration timeLimit = Duration.ofHours(4);
        TicketReaderInfo firstUsage = createAnyStation();

        LocalDateTime timeAtTestNotSurpassed = firstUsage.time().plusHours(1);
        Ticket ticket = new CommonTicketBuilder("4-Hour Ticket", UUID.randomUUID())
                .timeLimit(timeLimit, timeAtTestNotSurpassed)
                .addUsage(firstUsage)
                .build();
        boolean valid = ticket.isValid();

        assertThat(valid).isTrue();
    }

    @Test
    void isValid_ShouldBeTrue_WhenTimeLimitSurpassedBeforeEntry() {
        Duration timeLimit = Duration.ofHours(4);
        TicketReaderInfo firstUsage = createAnyStation();

        LocalDateTime timeAtTestSurpassed = firstUsage.time().plusHours(5);
        Ticket ticket = new CommonTicketBuilder("4-Hour Ticket", UUID.randomUUID())
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
        Ticket ticket = new CommonTicketBuilder("4-Hour Ticket", UUID.randomUUID())
                .timeLimit(timeLimit, timeAtTest)
                .build();

        boolean valid = ticket.isValid();

        assertThat(valid).isTrue();
    }

    @Test
    void isValid_ShouldBeTrue_WhenTimeLimitIsZero() {
        Duration timeLimit = Duration.ofHours(0);
        LocalDateTime timeAtTest = LocalDateTime.now().plusSeconds(1);
        TicketReaderInfo firstUsage = createAnyStation();
        Ticket ticket = new CommonTicketBuilder("Valid Ticket", UUID.randomUUID())
                .timeLimit(timeLimit, timeAtTest)
                .addUsage(firstUsage)
                .build();

        boolean valid = ticket.isValid();

        assertThat(valid).isTrue();
    }

    private TicketReaderInfo createAnyStation() {
        return new TicketEntryReader(new SimpleStation("any"));
    }

}