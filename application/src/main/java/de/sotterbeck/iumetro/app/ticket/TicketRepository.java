package de.sotterbeck.iumetro.app.ticket;

import de.sotterbeck.iumetro.app.faregate.UsageDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository {

    void save(TicketDto ticket);

    Optional<TicketDto> get(UUID id);

    List<TicketDto> getAll();

    boolean existsById(UUID id);

    void deleteById(UUID id);

    List<UsageDto> getTicketUsages(UUID uuid);

    void saveTicketUsage(UUID ticketId, UsageDto usage);

}
