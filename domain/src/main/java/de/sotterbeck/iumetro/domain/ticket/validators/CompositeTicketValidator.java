package de.sotterbeck.iumetro.domain.ticket.validators;

import de.sotterbeck.iumetro.domain.ticket.Ticket;
import de.sotterbeck.iumetro.domain.ticket.ValidationContext;
import de.sotterbeck.iumetro.domain.ticket.ValidationResult;

import java.util.ArrayList;
import java.util.List;

public record CompositeTicketValidator(List<TicketValidator> validators) implements TicketValidator {

    @Override
    public ValidationResult validate(Ticket ticket, ValidationContext context) {
        if (validators.isEmpty()) {
            return ValidationResult.allowAndRecord();
        }

        boolean allowGate = true;
        boolean recordUsage = true;
        boolean removeTicket = false;
        String reason = null;

        for (TicketValidator validator : validators) {
            ValidationResult result = validator.validate(ticket, context);
            allowGate = allowGate && result.allowGate();
            recordUsage = recordUsage && result.recordUsage();
            removeTicket = removeTicket || result.removeTicket();
            if (reason == null && result.reason() != null) {
                reason = result.reason();
            }
        }

        return new ValidationResult(allowGate, recordUsage, removeTicket, reason);
    }

    @Override
    public List<TicketValidator> validators() {
        return new ArrayList<>(validators);
    }

}
