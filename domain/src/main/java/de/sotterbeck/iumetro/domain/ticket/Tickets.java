package de.sotterbeck.iumetro.domain.ticket;

import de.sotterbeck.iumetro.domain.ticket.validators.TicketValidator;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.StreamSupport;

public final class Tickets {

    private Tickets() {
    }

    public static Ticket createTicket(String name, UUID id, Iterable<TicketValidator> validators) {
        Objects.requireNonNull(name, "Ticket name must not be null.");
        Objects.requireNonNull(id, "Ticket id must not be null.");
        Objects.requireNonNull(validators, "Validators must not be null.");

        var validatorList = StreamSupport.stream(validators.spliterator(), false).toList();
        return new SimpleTicket(name, id, validatorList);
    }

}
