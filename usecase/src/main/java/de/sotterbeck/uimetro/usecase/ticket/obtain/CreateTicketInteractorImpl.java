package de.sotterbeck.uimetro.usecase.ticket.obtain;

import de.sotterbeck.uimetro.usecase.ticket.TicketDsGateway;
import de.sotterbeck.uimetro.usecase.ticket.TicketDsRequestModel;
import de.sotterbeck.uimetro.usecase.ticket.TicketPrintHandler;
import de.sotterbeck.uimetro.usecase.ticket.TicketRequestModel;

import java.util.UUID;

public class CreateTicketInteractorImpl implements CreateTicketInteractor {

    private final TicketDsGateway ticketDsGateway;
    private final TicketPrintHandler ticketPrintHandler;

    public CreateTicketInteractorImpl(TicketDsGateway ticketDsGateway, TicketPrintHandler ticketPrintHandler) {
        this.ticketDsGateway = ticketDsGateway;
        this.ticketPrintHandler = ticketPrintHandler;
    }

    @Override
    public void invoke(TicketRequestModel ticket) {
        UUID id = UUID.randomUUID();
        ticketDsGateway.save(toDsTicket(ticket, id));
        ticketPrintHandler.printTicket(ticket, id);
    }

    private static TicketDsRequestModel toDsTicket(TicketRequestModel ticket, UUID id) {
        return new TicketDsRequestModel(id, ticket.name(), ticket.usageLimit(), ticket.timeLimit());
    }

}
