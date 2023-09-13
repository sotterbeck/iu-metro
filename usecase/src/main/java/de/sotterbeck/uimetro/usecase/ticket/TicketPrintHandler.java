package de.sotterbeck.uimetro.usecase.ticket;

import java.util.UUID;

public interface TicketPrintHandler {

    void printTicket(TicketRequestModel ticketDsRequestModel, UUID ticketId);

}
