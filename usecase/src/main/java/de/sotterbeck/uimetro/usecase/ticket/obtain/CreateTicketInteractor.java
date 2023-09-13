package de.sotterbeck.uimetro.usecase.ticket.obtain;

import de.sotterbeck.uimetro.usecase.ticket.TicketRequestModel;

public interface CreateTicketInteractor {

    void invoke(TicketRequestModel ticket);

}
