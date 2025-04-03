package de.sotterbeck.iumetro.app.ticket;

import de.sotterbeck.iumetro.app.faregate.UsageResponseModel;

import java.util.List;
import java.util.UUID;

public interface TicketInfoInteractor {

    boolean exists(String ticketId);

    List<UsageResponseModel> usages(UUID id);

}
