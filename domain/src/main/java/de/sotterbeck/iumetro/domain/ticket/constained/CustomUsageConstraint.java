package de.sotterbeck.iumetro.domain.ticket.constained;

import de.sotterbeck.iumetro.domain.ticket.Ticket;

import java.util.function.Predicate;

public class CustomUsageConstraint implements TicketUsageConstraint {

    private final Predicate<Ticket> isValid;

    private CustomUsageConstraint(Predicate<Ticket> validationTest) {
        this.isValid = validationTest;
    }

    public static TicketUsageConstraint ofTest(Predicate<Ticket> validationTest) {
        return new CustomUsageConstraint(validationTest);
    }

    @Override
    public boolean isValid(Ticket ticket) {
        return isValid.test(ticket);
    }

}
