package de.sotterbeck.iumetro.usecase.ticket.manage;

import java.util.List;
import java.util.UUID;

public interface GetAllTicketsInteractor {

    List<UUID> invoke();

}
