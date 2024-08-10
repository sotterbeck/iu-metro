package de.sotterbeck.iumetro.usecase.ticket;

import de.sotterbeck.iumetro.usecase.faregate.UsageResponseModel;

import java.util.List;
import java.util.UUID;

public interface TicketInfoInteractor {

    boolean exists(String ticketId);

    List<UsageResponseModel> usages(UUID id);

}
