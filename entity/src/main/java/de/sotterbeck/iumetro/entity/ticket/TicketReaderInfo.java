package de.sotterbeck.iumetro.entity.ticket;

import java.time.LocalDateTime;

public interface TicketReaderInfo {

    Station station();

    LocalDateTime time();

    UsageType usageType();

}
