package de.sotterbeck.iumetro.domain.ticket.validators;

import de.sotterbeck.iumetro.domain.ticket.Ticket;
import de.sotterbeck.iumetro.domain.ticket.ValidationContext;
import de.sotterbeck.iumetro.domain.ticket.ValidationResult;

public interface TicketValidator {

    ValidationResult validate(Ticket ticket, ValidationContext context);

}
