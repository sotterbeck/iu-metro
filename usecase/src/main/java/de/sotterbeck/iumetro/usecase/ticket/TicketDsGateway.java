package de.sotterbeck.iumetro.usecase.ticket;

import java.util.Optional;
import java.util.UUID;

public interface TicketDsGateway {

    void save(TicketDsRequestModel ticket);

    Optional<TicketDsRequestModel> get(UUID id);

    boolean existsById(UUID id);

    void deleteById(UUID id);

}
