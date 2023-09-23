package de.sotterbeck.iumetro.usecase.ticket.obtain;

import java.util.UUID;

public interface DeleteTicketInteractor {

    void invoke(UUID ticketId);

}
