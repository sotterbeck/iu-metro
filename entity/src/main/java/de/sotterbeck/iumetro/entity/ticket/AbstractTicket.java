package de.sotterbeck.iumetro.entity.ticket;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

abstract class AbstractTicket implements Ticket {

    private final String name;
    private final UUID id;
    private final List<TicketReaderInfo> usages;

    AbstractTicket(String name, UUID id, List<TicketReaderInfo> usages) {
        this.name = name;
        this.id = id;
        this.usages = usages;
    }

    public String name() {
        return name;
    }

    public UUID id() {
        return id;
    }

    @Override
    public List<TicketReaderInfo> usages() {
        return Collections.unmodifiableList(usages);
    }

    @Override
    public void addUsage(TicketReaderInfo ticketReader) {
        usages.add(ImmutableTicketReaderInfo.ofReader(ticketReader));
    }

}
