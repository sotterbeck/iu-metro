package de.sotterbeck.iumetro.usecase.ticket.obtain;

import de.sotterbeck.iumetro.usecase.ticket.TicketRequestModel;
import de.sotterbeck.iumetro.usecase.ticket.TicketResponseModel;

public interface CreateTicketInteractor {

    TicketResponseModel create(TicketRequestModel ticket);

}
