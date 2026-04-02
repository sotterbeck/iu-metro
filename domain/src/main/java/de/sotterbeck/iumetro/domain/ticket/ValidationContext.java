package de.sotterbeck.iumetro.domain.ticket;

import java.util.List;
import java.util.Objects;

public record ValidationContext(List<TicketUsage> usages, TicketUsage attemptedUsage) {

    public ValidationContext {
        Objects.requireNonNull(usages, "Usages must not be null.");
        Objects.requireNonNull(attemptedUsage, "Attempted usage must not be null.");
    }

}
