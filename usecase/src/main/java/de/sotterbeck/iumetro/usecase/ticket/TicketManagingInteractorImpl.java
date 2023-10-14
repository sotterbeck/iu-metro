package de.sotterbeck.iumetro.usecase.ticket;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class TicketManagingInteractorImpl implements TicketManagingInteractor {

    private final TicketDsGateway ticketDsGateway;
    private final TicketPresenter ticketPresenter;
    private final TicketPrintingHandler printingHandler;

    public TicketManagingInteractorImpl(TicketDsGateway ticketDsGateway, TicketPresenter ticketPresenter) {
        this(ticketDsGateway, ticketPresenter, new NoTicketPrintingHandler());
    }

    public TicketManagingInteractorImpl(TicketDsGateway ticketDsGateway, TicketPresenter ticketPresenter, TicketPrintingHandler printingHandler) {
        this.ticketDsGateway = ticketDsGateway;
        this.ticketPresenter = ticketPresenter;
        this.printingHandler = printingHandler;
    }

    @Override
    public TicketResponseModel create(TicketRequestModel ticketRequestModel) {
        ticketDsGateway.save(toDsModel(ticketRequestModel));
        TicketResponseModel ticketResponse = ticketPresenter.prepareSuccessView(ticketRequestModel);
        printingHandler.printTicket(ticketResponse);
        return ticketResponse;
    }

    @Override
    public TicketResponseModel delete(UUID id) {
        if (!ticketDsGateway.existsById(id)) {
            return ticketPresenter.prepareFailView("Ticket with id %s does not exists.".formatted(id));
        }
        TicketDsModel ticket = ticketDsGateway.get(id).orElseThrow(AssertionError::new);
        ticketDsGateway.deleteById(id);
        return ticketPresenter.prepareSuccessView(toRequestModel(ticket));
    }

    @Override
    public List<String> getAllIds() {
        return ticketDsGateway.getAll().stream()
                .map(ticketDsModel -> ticketPresenter.prepareSuccessView(toRequestModel(ticketDsModel)))
                .map(TicketResponseModel::fullId)
                .toList();
    }

    @NotNull
    private TicketRequestModel toRequestModel(TicketDsModel ticket) {
        return new TicketRequestModel(ticket.id(),
                ticket.name(),
                ticket.usageLimit(),
                ticket.timeLimit());
    }

    @NotNull
    private TicketDsModel toDsModel(TicketRequestModel ticketRequestModel) {
        return new TicketDsModel(ticketRequestModel.id(),
                ticketRequestModel.name(),
                ticketRequestModel.usageLimit(),
                ticketRequestModel.timeLimit());
    }

    private static class NoTicketPrintingHandler implements TicketPrintingHandler {

        @Override
        public void printTicket(TicketResponseModel ticketResponseModel) {

        }

    }

}
