package de.sotterbeck.iumetro.usecase.ticket.obtain;

import de.sotterbeck.iumetro.usecase.ticket.TicketResponseModel;

public interface TicketPrintingGateway {

    void printTicket(TicketResponseModel ticketResponseModel);

}
