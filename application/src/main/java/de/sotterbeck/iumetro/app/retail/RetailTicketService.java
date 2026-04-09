package de.sotterbeck.iumetro.app.retail;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.*;

public class RetailTicketService {

    private final RetailTicketRepository retailTicketRepository;
    private final RetailTicketPresenter presenter;

    public RetailTicketService(RetailTicketRepository retailTicketRepository, RetailTicketPresenter presenter) {
        this.retailTicketRepository = retailTicketRepository;
        this.presenter = presenter;
    }

    public List<RetailTicketResponseModel> getAll() {
        Collection<RetailTicketDto> allRetailTickets = retailTicketRepository.getAll();
        return allRetailTickets.stream()
                .map(RetailTicketService::toResponseModel)
                .toList();
    }

    public Optional<RetailTicketResponseModel> getById(String id) {
        UUID uuid = UUID.fromString(id);
        return retailTicketRepository.getById(uuid)
                .map(RetailTicketService::toResponseModel);
    }

    public Map<String, List<RetailTicketResponseModel>> getAllGroupedByCategory() {
        Map<String, Collection<RetailTicketDto>> grouped = retailTicketRepository.getAllGroupedByCategory();
        return grouped.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(RetailTicketService::toResponseModel)
                                .toList()
                ));
    }

    public RetailTicketResponseModel create(RetailTicketRequestModel retailTicket) {
        if (retailTicketRepository.existsByName(retailTicket.name())) {
            return presenter.prepareFailView("Retail ticket with name " + retailTicket.name() + " already exists");
        }

        RetailTicketDto retailTicketDto = toDto(retailTicket);
        retailTicketRepository.save(retailTicketDto);

        return presenter.prepareSuccessView(retailTicket);
    }

    public RetailTicketResponseModel update(String id, RetailTicketRequestModel retailTicket) {
        UUID uuid = UUID.fromString(id);
        if (!retailTicketRepository.existsById(uuid)) {
            return presenter.prepareFailView("Retail ticket with id " + id + " does not exist");
        }

        RetailTicketDto updated = new RetailTicketDto(
                UUID.fromString(id),
                retailTicket.name(),
                retailTicket.description(),
                retailTicket.priceCents(),
                retailTicket.config(),
                retailTicket.isActive(),
                Instant.now(),
                retailTicket.category()
        );

        retailTicketRepository.save(updated);

        return presenter.prepareSuccessView(retailTicket);
    }

    public RetailTicketResponseModel delete(String id) {
        UUID uuid = UUID.fromString(id);
        Optional<RetailTicketDto> retailTicket = retailTicketRepository.getById(uuid);
        if (retailTicket.isEmpty()) {
            return presenter.prepareFailView("Retail ticket with id " + id + " does not exist");
        }

        retailTicketRepository.delete(uuid);

        RetailTicketDto deleted = retailTicket.orElseThrow();
        RetailTicketRequestModel requestedTicket = new RetailTicketRequestModel(
                deleted.id().toString(), deleted.name(), deleted.description(), deleted.priceCents(), deleted.config(), deleted.isActive(), deleted.category()
        );

        return presenter.prepareSuccessView(requestedTicket);
    }

    private static @NotNull RetailTicketDto toDto(RetailTicketRequestModel retailTicket) {
        return new RetailTicketDto(
                UUID.fromString(retailTicket.id()),
                retailTicket.name(),
                retailTicket.description(),
                0, retailTicket.config(), retailTicket.isActive(), Instant.now(), retailTicket.category()
        );
    }

    private static @NotNull RetailTicketResponseModel toResponseModel(RetailTicketDto dto) {
        return new RetailTicketResponseModel(dto.id().toString(),
                dto.name(),
                dto.description(),
                0L, dto.config(), dto.isActive(), dto.createdAt().toString(), dto.category()
        );
    }

}
