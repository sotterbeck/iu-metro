package de.sotterbeck.iumetro.usecase.ticket.obtain;

import de.sotterbeck.iumetro.usecase.ticket.TicketResponseModel;

import java.util.UUID;

public interface DeleteTicketInteractor {

    TicketResponseModel delete(UUID ticketId);

}
