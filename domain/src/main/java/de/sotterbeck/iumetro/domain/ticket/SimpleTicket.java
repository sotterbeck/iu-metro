package de.sotterbeck.iumetro.domain.ticket;

import de.sotterbeck.iumetro.domain.ticket.validators.CompositeTicketValidator;
import de.sotterbeck.iumetro.domain.ticket.validators.TicketValidator;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

class SimpleTicket implements Ticket {

    private final String name;
    private final UUID id;
    private final TicketValidator validator;

    public SimpleTicket(String name, UUID id, List<TicketValidator> validators) {
        Objects.requireNonNull(name, "Ticket name must not be null.");
        Objects.requireNonNull(id, "Ticket id must not be null.");
        Objects.requireNonNull(validators, "Validators must not be null.");

        this.name = name;
        this.id = id;
        this.validator = new CompositeTicketValidator(validators);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public UUID id() {
        return id;
    }

    @Override
    public ValidationResult validate(ValidationContext context) {
        return validator.validate(this, context);
    }

}
