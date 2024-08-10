package de.sotterbeck.iumetro.entity.ticket.constained;

import de.sotterbeck.iumetro.entity.reader.TicketReaderInfo;
import de.sotterbeck.iumetro.entity.ticket.Ticket;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Predicate;

public interface ConstrainedTicketBuilder {

    ConstrainedTicketBuilder timeLimit(Duration timeLimit, LocalDateTime timeAtTest);

    ConstrainedTicketBuilder usageLimit(int limit);

    ConstrainedTicketBuilder customLimit(Predicate<Ticket> validationTest);

    ConstrainedTicketBuilder addUsage(TicketReaderInfo usage);

    Ticket build();

}
