package de.sotterbeck.iumetro.domain.ticket.validators;

import de.sotterbeck.iumetro.domain.ticket.Ticket;
import de.sotterbeck.iumetro.domain.ticket.UsageType;
import de.sotterbeck.iumetro.domain.ticket.ValidationContext;
import de.sotterbeck.iumetro.domain.ticket.ValidationResult;

public class UsageLimitValidator implements TicketValidator {

    public static final String USAGE_LIMIT_REACHED = "usage_limit_reached";
    private final int limit;

    public UsageLimitValidator(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("Usage limit must not be negative.");
        }
        this.limit = limit;
    }

    @Override
    public ValidationResult validate(Ticket ticket, ValidationContext context) {
        if (limit <= 0) {
            return ValidationResult.allowAndRecord();
        }

        long entryCount = context.usages().stream()
                .filter(usage -> usage.usageType() == UsageType.ENTRY)
                .count();

        if (entryCount < limit) {
            return ValidationResult.allowAndRecord();
        }

        if (context.attemptedUsage().usageType() == UsageType.EXIT) {
            return ValidationResult.allowRecordRemove(USAGE_LIMIT_REACHED);
        }

        return ValidationResult.deny(USAGE_LIMIT_REACHED);
    }

}
