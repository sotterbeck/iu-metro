package de.sotterbeck.iumetro.usecase.ticket.obtain;

import de.sotterbeck.iumetro.usecase.ticket.TicketRequestModel;

public interface CreateTicketInteractor {

    void invoke(TicketRequestModel ticket);

}
