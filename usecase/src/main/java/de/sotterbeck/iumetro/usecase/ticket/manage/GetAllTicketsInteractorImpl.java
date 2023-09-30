package de.sotterbeck.iumetro.usecase.ticket.manage;

import de.sotterbeck.iumetro.usecase.ticket.TicketDsGateway;
import de.sotterbeck.iumetro.usecase.ticket.TicketDsModel;

import java.util.List;
import java.util.UUID;

public class GetAllTicketsInteractorImpl implements GetAllTicketsInteractor {

    private final TicketDsGateway ticketDsGateway;

    public GetAllTicketsInteractorImpl(TicketDsGateway ticketDsGateway) {
        this.ticketDsGateway = ticketDsGateway;
    }

    @Override
    public List<UUID> invoke() {
        return ticketDsGateway.getAll().stream()
                .map(TicketDsModel::id)
                .toList();
    }

}
