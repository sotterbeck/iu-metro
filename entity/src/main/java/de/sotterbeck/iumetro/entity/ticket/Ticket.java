package de.sotterbeck.iumetro.entity.ticket;

import de.sotterbeck.iumetro.entity.reader.TicketReaderInfo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Ticket {

    String name();

    UUID id();

    void addUsage(TicketReaderInfo ticketReader);

    default void onEntry(TicketReaderInfo ticketReader) {
        addUsage(ImmutableTicketReaderInfo.ofReader(ticketReader));
    }

    default void onExit(TicketReaderInfo ticketReader) {
        addUsage(ImmutableTicketReaderInfo.ofReader(ticketReader));
    }

    List<TicketReaderInfo> usages();

    default int usageCount() {
        return usages().size();
    }

    default Optional<TicketReaderInfo> firstUsage() {
        return usages().isEmpty()
                ? Optional.empty()
                : Optional.ofNullable(usages().get(0));
    }

    default Optional<TicketReaderInfo> lastUsage() {
        return usages().isEmpty()
                ? Optional.empty()
                : Optional.ofNullable(usages().get(usages().size() - 1));

    }

    boolean isValid();

    default boolean isInSystem() {
        return lastUsage().map(TicketReaderInfo::usageType)
                .map(usageType -> UsageType.ENTRY == usageType)
                .orElse(false);

    }

}
