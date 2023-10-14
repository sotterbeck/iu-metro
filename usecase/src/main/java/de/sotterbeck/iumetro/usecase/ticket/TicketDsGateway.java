package de.sotterbeck.iumetro.usecase.ticket;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketDsGateway {

    void save(TicketDsModel ticket);

    Optional<TicketDsModel> get(UUID id);

    List<TicketDsModel> getAll();

    boolean existsById(UUID id);

    void deleteById(UUID id);

    List<UsageDsModel> getTicketUsages(UUID uuid);

}
