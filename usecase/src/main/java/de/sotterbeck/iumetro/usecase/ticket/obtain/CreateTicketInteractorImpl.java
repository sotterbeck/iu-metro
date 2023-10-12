package de.sotterbeck.iumetro.usecase.ticket.obtain;

import de.sotterbeck.iumetro.usecase.ticket.*;

public class CreateTicketInteractorImpl implements CreateTicketInteractor {

    private final TicketDsGateway ticketDsGateway;
    private final TicketPresenter ticketPresenter;
    private final TicketPrintingGateway ticketPrintingGateway;

    public CreateTicketInteractorImpl(TicketDsGateway ticketDsGateway,
                                      TicketPresenter ticketPresenter,
                                      TicketPrintingGateway ticketPrintingGateway) {
        this.ticketDsGateway = ticketDsGateway;
        this.ticketPresenter = ticketPresenter;
        this.ticketPrintingGateway = ticketPrintingGateway;
    }

    @Override
    public TicketResponseModel create(TicketRequestModel ticket) {
        ticketDsGateway.save(toDsTicket(ticket));
        TicketResponseModel ticketResponse = ticketPresenter.prepareSuccessView(ticket);
        ticketPrintingGateway.printTicket(ticketResponse);
        return ticketResponse;
    }

    private static TicketDsModel toDsTicket(TicketRequestModel ticket) {
        return new TicketDsModel(ticket.id(), ticket.name(), ticket.usageLimit(), ticket.timeLimit());
    }

}
