package de.sotterbeck.iumetro.usecase.ticket.obtain;

import de.sotterbeck.iumetro.usecase.ticket.TicketDsGateway;
import de.sotterbeck.iumetro.usecase.ticket.TicketDsModel;
import de.sotterbeck.iumetro.usecase.ticket.TicketPresenter;
import de.sotterbeck.iumetro.usecase.ticket.TicketRequestModel;

public class CreateTicketInteractorImpl implements CreateTicketInteractor {

    private final TicketDsGateway ticketDsGateway;
    private final TicketPresenter ticketPresenter;

    public CreateTicketInteractorImpl(TicketDsGateway ticketDsGateway, TicketPresenter ticketPresenter) {
        this.ticketDsGateway = ticketDsGateway;
        this.ticketPresenter = ticketPresenter;
    }

    @Override
    public void invoke(TicketRequestModel ticket) {
        ticketDsGateway.save(toDsTicket(ticket));
        preparePresenter(ticket);
    }

    private void preparePresenter(TicketRequestModel ticket) {
        ticketPresenter.printTicket(ticket);
        ticketPresenter.prepareSuccessView(ticket, "You created a ticket with the id %s".formatted(ticket.id()));
    }

    private static TicketDsModel toDsTicket(TicketRequestModel ticket) {
        return new TicketDsModel(ticket.id(), ticket.name(), ticket.usageLimit(), ticket.timeLimit());
    }

}
