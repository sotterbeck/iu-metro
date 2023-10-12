package de.sotterbeck.iumetro.usecase.ticket.obtain;

import de.sotterbeck.iumetro.usecase.ticket.*;

import java.util.UUID;

public class DeleteTicketInteractorImpl implements DeleteTicketInteractor {

    private final TicketDsGateway ticketDsGateway;
    private final TicketPresenter ticketPresenter;

    public DeleteTicketInteractorImpl(TicketDsGateway ticketDsGateway, TicketPresenter ticketPresenter) {
        this.ticketDsGateway = ticketDsGateway;
        this.ticketPresenter = ticketPresenter;
    }

    @Override
    public TicketResponseModel delete(UUID ticketId) {
        if (!ticketDsGateway.existsById(ticketId)) {
            return ticketPresenter.prepareFailView("Ticket with id %s does not exists.".formatted(ticketId));
        }
        TicketDsModel ticket = ticketDsGateway.get(ticketId).orElseThrow(AssertionError::new);
        ticketDsGateway.deleteById(ticketId);
        return ticketPresenter.prepareSuccessView(toRequestModel(ticket));
    }

    private static TicketRequestModel toRequestModel(TicketDsModel ticket) {
        return new TicketRequestModel(ticket.id(),
                ticket.name(),
                ticket.usageLimit(),
                ticket.timeLimit());
    }

}
