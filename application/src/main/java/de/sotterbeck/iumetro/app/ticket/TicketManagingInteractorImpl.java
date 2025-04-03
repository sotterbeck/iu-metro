package de.sotterbeck.iumetro.app.ticket;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class TicketManagingInteractorImpl implements TicketManagingInteractor {

    private final TicketRepository ticketRepository;
    private final TicketPresenter ticketPresenter;

    public TicketManagingInteractorImpl(TicketRepository ticketRepository, TicketPresenter ticketPresenter) {
        this.ticketRepository = ticketRepository;
        this.ticketPresenter = ticketPresenter;
    }

    @Override
    public TicketResponseModel create(TicketRequestModel ticketRequestModel) {
        ticketRepository.save(toDto(ticketRequestModel));
        return ticketPresenter.prepareSuccessView(ticketRequestModel);
    }

    @Override
    public TicketResponseModel delete(UUID id) {
        if (!ticketRepository.existsById(id)) {
            return ticketPresenter.prepareFailView("Ticket with id %s does not exists.".formatted(id));
        }
        TicketDto ticket = ticketRepository.get(id).orElseThrow(AssertionError::new);
        ticketRepository.deleteById(id);
        return ticketPresenter.prepareSuccessView(toRequestModel(ticket));
    }

    @Override
    public List<String> getAllIds() {
        return ticketRepository.getAll().stream()
                .map(ticketDto -> ticketPresenter.prepareSuccessView(toRequestModel(ticketDto)))
                .map(TicketResponseModel::fullId)
                .toList();
    }

    @NotNull
    private TicketRequestModel toRequestModel(TicketDto ticket) {
        return new TicketRequestModel(ticket.id(),
                ticket.name(),
                ticket.usageLimit(),
                ticket.timeLimit());
    }

    @NotNull
    private TicketDto toDto(TicketRequestModel ticketRequestModel) {
        return new TicketDto(ticketRequestModel.id(),
                ticketRequestModel.name(),
                ticketRequestModel.usageLimit(),
                ticketRequestModel.timeLimit());
    }
}
