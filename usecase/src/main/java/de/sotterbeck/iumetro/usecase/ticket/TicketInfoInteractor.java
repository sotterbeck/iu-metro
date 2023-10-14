package de.sotterbeck.iumetro.usecase.ticket;

import java.util.List;
import java.util.UUID;

public interface TicketInfoInteractor {

    boolean exists(String ticketId);

    List<UsageResponseModel> usages(UUID id);

}
