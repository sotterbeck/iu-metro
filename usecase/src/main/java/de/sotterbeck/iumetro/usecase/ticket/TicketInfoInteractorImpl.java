package de.sotterbeck.iumetro.usecase.ticket;

import java.util.UUID;

public class TicketInfoInteractorImpl implements TicketInfoInteractor {

    private final TicketDsGateway ticketDsGateway;

    public TicketInfoInteractorImpl(TicketDsGateway ticketDsGateway) {
        this.ticketDsGateway = ticketDsGateway;
    }

    @Override
    public boolean exists(String ticketId) {
        UUID id = UUID.fromString(ticketId);
        return ticketDsGateway.existsById(id);
    }

}
