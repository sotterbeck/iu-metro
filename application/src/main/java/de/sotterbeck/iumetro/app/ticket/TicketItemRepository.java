package de.sotterbeck.iumetro.app.ticket;

import java.util.Optional;
import java.util.UUID;

public interface TicketItemRepository {

    Optional<UUID> findCurrentTicket(UUID playerId);

    boolean deleteTicket(UUID playerId, UUID ticketId);

}
