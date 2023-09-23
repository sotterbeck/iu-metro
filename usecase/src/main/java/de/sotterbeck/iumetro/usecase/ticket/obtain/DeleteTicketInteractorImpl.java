package de.sotterbeck.iumetro.usecase.ticket.obtain;

import de.sotterbeck.iumetro.usecase.ticket.TicketDsGateway;
import de.sotterbeck.iumetro.usecase.ticket.TicketDsRequestModel;
import de.sotterbeck.iumetro.usecase.ticket.TicketPresenter;
import de.sotterbeck.iumetro.usecase.ticket.TicketRequestModel;

import java.util.UUID;

public class DeleteTicketInteractorImpl implements DeleteTicketInteractor {

    private final TicketDsGateway ticketDsGateway;
    private final TicketPresenter ticketPresenter;

    public DeleteTicketInteractorImpl(TicketDsGateway ticketDsGateway, TicketPresenter ticketPresenter) {
        this.ticketDsGateway = ticketDsGateway;
        this.ticketPresenter = ticketPresenter;
    }

    @Override
    public void invoke(UUID ticketId) {
        if (!ticketDsGateway.existsById(ticketId)) {
            ticketPresenter.prepareFailView("Ticket with id " + ticketId + " does not exists.");
            return;
        }
        TicketDsRequestModel ticket = ticketDsGateway.get(ticketId).orElseThrow(AssertionError::new);
        ticketDsGateway.deleteById(ticketId);
        ticketPresenter.prepareSuccessView(toRequestModel(ticket));
    }

    private static TicketRequestModel toRequestModel(TicketDsRequestModel ticket) {
        return new TicketRequestModel(ticket.id(),
                ticket.name(),
                ticket.usageLimit(),
                ticket.timeLimit());
    }

}
