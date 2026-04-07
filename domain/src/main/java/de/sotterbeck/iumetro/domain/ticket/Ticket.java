package de.sotterbeck.iumetro.domain.ticket;

import java.util.UUID;

public interface Ticket {

    String name();

    UUID id();

    ValidationResult validate(ValidationContext context);

}
