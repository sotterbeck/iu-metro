package de.sotterbeck.iumetro.entity.ticket;

import java.util.function.Predicate;

class CustomUsageConstraint implements TicketUsageConstraint {

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
