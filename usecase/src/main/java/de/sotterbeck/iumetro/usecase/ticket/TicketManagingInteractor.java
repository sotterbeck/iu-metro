package de.sotterbeck.iumetro.usecase.ticket;

import java.util.List;
import java.util.UUID;

public interface TicketManagingInteractor {

    TicketResponseModel create(TicketRequestModel ticketRequestModel);

    TicketResponseModel delete(UUID id);

    List<String> getAllIds();

}
