package de.sotterbeck.iumetro.domain.ticket;

import de.sotterbeck.iumetro.domain.ticket.validators.TimeLimitValidator;
import de.sotterbeck.iumetro.domain.ticket.validators.UsageLimitValidator;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;

class TicketValidationTest {

    @Test
    void validate_ShouldAllow_WhenNoConstraints() {
        Ticket ticket = new SimpleTicket("Employee Ticket", UUID.randomUUID(), List.of());
        TicketUsage attemptedUsage = usageAt(ZonedDateTime.now(), UsageType.ENTRY);
        ValidationResult result = ticket.validate(new ValidationContext(List.of(), attemptedUsage));

        assertThat(result.allowGate()).isTrue();
        assertThat(result.recordUsage()).isTrue();
        assertThat(result.removeTicket()).isFalse();
    }

    @Test
    void timeLimit_ShouldThrow_WhenNegative() {
        Duration timeLimit = Duration.ofHours(-1);

        Throwable thrown = catchThrowable(() -> new TimeLimitValidator(timeLimit));

        then(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void usageLimit_ShouldThrow_WhenNegative() {
        Throwable thrown = catchThrowable(() -> new UsageLimitValidator(-1));

        then(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void timeLimit_ShouldAllow_WhenWithinLimit() {
        ZonedDateTime start = ZonedDateTime.now();
        TicketUsage entry = usageAt(start, UsageType.ENTRY);
        TicketUsage attemptedUsage = usageAt(start.plusHours(1), UsageType.ENTRY);
        Ticket ticket = new SimpleTicket("4-Hour Ticket", UUID.randomUUID(),
                List.of(new TimeLimitValidator(Duration.ofHours(4))));

        ValidationResult result = ticket.validate(new ValidationContext(List.of(entry), attemptedUsage));

        assertThat(result.allowGate()).isTrue();
        assertThat(result.recordUsage()).isTrue();
        assertThat(result.removeTicket()).isFalse();
    }

    @Test
    void timeLimit_ShouldDenyEntry_WhenExpired() {
        ZonedDateTime start = ZonedDateTime.now();
        TicketUsage entry = usageAt(start, UsageType.ENTRY);
        TicketUsage attemptedUsage = usageAt(start.plusHours(5), UsageType.ENTRY);
        Ticket ticket = new SimpleTicket("4-Hour Ticket", UUID.randomUUID(),
                List.of(new TimeLimitValidator(Duration.ofHours(4))));

        ValidationResult result = ticket.validate(new ValidationContext(List.of(entry), attemptedUsage));

        assertThat(result.allowGate()).isFalse();
        assertThat(result.recordUsage()).isFalse();
        assertThat(result.removeTicket()).isFalse();
        assertThat(result.reason()).isEqualTo("time_expired");
    }

    @Test
    void timeLimit_ShouldAllowExitAndRemove_WhenExpired() {
        ZonedDateTime start = ZonedDateTime.now();
        TicketUsage entry = usageAt(start, UsageType.ENTRY);
        TicketUsage attemptedUsage = usageAt(start.plusHours(5), UsageType.EXIT);
        Ticket ticket = new SimpleTicket("4-Hour Ticket", UUID.randomUUID(),
                List.of(new TimeLimitValidator(Duration.ofHours(4))));

        ValidationResult result = ticket.validate(new ValidationContext(List.of(entry), attemptedUsage));

        assertThat(result.allowGate()).isTrue();
        assertThat(result.recordUsage()).isTrue();
        assertThat(result.removeTicket()).isTrue();
        assertThat(result.reason()).isEqualTo("time_expired");
    }

    @Test
    void usageLimit_ShouldDenyEntry_WhenLimitReached() {
        TicketUsage entry = usageAt(ZonedDateTime.now(), UsageType.ENTRY);
        TicketUsage attemptedUsage = usageAt(ZonedDateTime.now().plusMinutes(1), UsageType.ENTRY);
        Ticket ticket = new SimpleTicket("Single-use Ticket", UUID.randomUUID(),
                List.of(new UsageLimitValidator(1)));

        ValidationResult result = ticket.validate(new ValidationContext(List.of(entry), attemptedUsage));

        assertThat(result.allowGate()).isFalse();
        assertThat(result.recordUsage()).isFalse();
        assertThat(result.removeTicket()).isFalse();
        assertThat(result.reason()).isEqualTo("usage_limit_reached");
    }

    @Test
    void usageLimit_ShouldAllowExitAndRemove_WhenLimitReached() {
        TicketUsage entry = usageAt(ZonedDateTime.now(), UsageType.ENTRY);
        TicketUsage attemptedUsage = usageAt(ZonedDateTime.now().plusMinutes(1), UsageType.EXIT);
        Ticket ticket = new SimpleTicket("Single-use Ticket", UUID.randomUUID(),
                List.of(new UsageLimitValidator(1)));

        ValidationResult result = ticket.validate(new ValidationContext(List.of(entry), attemptedUsage));

        assertThat(result.allowGate()).isTrue();
        assertThat(result.recordUsage()).isTrue();
        assertThat(result.removeTicket()).isTrue();
        assertThat(result.reason()).isEqualTo("usage_limit_reached");
    }

    private TicketUsage usageAt(ZonedDateTime time, UsageType usageType) {
        return new TicketUsage(time, usageType);
    }

}
