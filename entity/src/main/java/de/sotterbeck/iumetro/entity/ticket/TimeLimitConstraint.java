package de.sotterbeck.iumetro.entity.ticket;

import java.time.Duration;
import java.time.LocalDateTime;

class TimeLimitConstraint implements TicketUsageConstraint {

    private final Duration timeLimit;
    private final LocalDateTime timeAtTest;

    /**
     * Class constructor.
     *
     * @param timeLimit  the positive duration within the ticket is valid.
     * @param timeAtTest the time at which the validation test is executed.
     */
    TimeLimitConstraint(Duration timeLimit, LocalDateTime timeAtTest) {
        if (timeLimit.isNegative()) {
            throw new IllegalArgumentException("The time limit should be positive.");
        }
        this.timeLimit = timeLimit;
        this.timeAtTest = timeAtTest;
    }

    @Override
    public boolean isValid(Ticket ticket) {
        return ticket.firstUsage()
                .map(TicketReaderInfo::time)
                .map(firstUsage -> firstUsage.plus(timeLimit))
                .map(expiry -> expiry.isAfter(timeAtTest))
                .orElse(true);
    }

}
