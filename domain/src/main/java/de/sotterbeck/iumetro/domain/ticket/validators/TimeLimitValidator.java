package de.sotterbeck.iumetro.domain.ticket.validators;

import de.sotterbeck.iumetro.domain.ticket.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class TimeLimitValidator implements TicketValidator {

    public static final String TIME_EXPIRED = "time_expired";
    private final Duration timeLimit;

    public TimeLimitValidator(Duration timeLimit) {
        if (timeLimit.isNegative()) {
            throw new IllegalArgumentException("The time limit should be positive.");
        }
        this.timeLimit = timeLimit;
    }

    @Override
    public ValidationResult validate(Ticket ticket, ValidationContext context) {
        if (timeLimit.isZero()) {
            return ValidationResult.allowAndRecord();
        }

        Optional<TicketUsage> firstUsage = context.usages().stream().findFirst();
        if (firstUsage.isEmpty()) {
            return ValidationResult.allowAndRecord();
        }

        LocalDateTime expiry = firstUsage.get().time().plus(timeLimit);
        boolean expired = expiry.isBefore(context.attemptedUsage().time());
        if (!expired) {
            return ValidationResult.allowAndRecord();
        }

        if (context.attemptedUsage().usageType() == UsageType.EXIT) {
            return ValidationResult.allowRecordRemove(TIME_EXPIRED);
        }

        return ValidationResult.deny(TIME_EXPIRED);
    }

}
