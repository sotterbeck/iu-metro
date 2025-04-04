package de.sotterbeck.iumetro.app.retail;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RetailTicketService {

    private final RetailTicketRepository retailTicketRepository;
    private final RetailTicketPresenter presenter;

    public RetailTicketService(RetailTicketRepository retailTicketRepository, RetailTicketPresenter presenter) {
        this.retailTicketRepository = retailTicketRepository;
        this.presenter = presenter;
    }

    List<RetailTicketResponseModel> getAll() {
        Collection<RetailTicketDto> allRetailTickets = retailTicketRepository.getAll();
        return allRetailTickets.stream()
                .map(RetailTicketService::toResponseModel)
                .toList();
    }

    RetailTicketResponseModel create(RetailTicketRequestModel retailTicket) {
        if (retailTicketRepository.existsByName(retailTicket.name())) {
            return presenter.prepareFailView("Retail ticket with name " + retailTicket.name() + " already exists");
        }

        RetailTicketDto retailTicketDto = toDto(retailTicket);
        retailTicketRepository.save(retailTicketDto);

        return presenter.prepareSuccessView(retailTicket);
    }

    RetailTicketResponseModel update(String id, RetailTicketRequestModel retailTicket) {
        UUID uuid = UUID.fromString(id);
        if (!retailTicketRepository.existsById(uuid)) {
            return presenter.prepareFailView("Retail ticket with id " + id + " does not exist");
        }

        RetailTicketDto updated = new RetailTicketDto(
                UUID.fromString(id),
                retailTicket.name(),
                retailTicket.description(),
                retailTicket.priceCents(),
                retailTicket.usageLimit(),
                Duration.parse(retailTicket.timeLimit()),
                retailTicket.isActive(),
                Instant.now(),
                retailTicket.category()
        );

        retailTicketRepository.save(updated);

        return presenter.prepareSuccessView(retailTicket);
    }

    RetailTicketResponseModel delete(String id) {
        UUID uuid = UUID.fromString(id);
        Optional<RetailTicketDto> retailTicket = retailTicketRepository.getById(uuid);
        if (retailTicket.isEmpty()) {
            return presenter.prepareFailView("Retail ticket with id " + id + " does not exist");
        }

        retailTicketRepository.delete(uuid);

        RetailTicketDto deleted = retailTicket.orElseThrow();
        RetailTicketRequestModel requestedTicket = new RetailTicketRequestModel(
                deleted.id().toString(), deleted.name(), deleted.description(), deleted.priceCents(), deleted.usageLimit(), deleted.timeLimit().toString(), deleted.isActive(), deleted.category()
        );

        return presenter.prepareSuccessView(requestedTicket);
    }

    private static @NotNull RetailTicketDto toDto(RetailTicketRequestModel retailTicket) {
        return new RetailTicketDto(
                UUID.fromString(retailTicket.id()),
                retailTicket.name(),
                retailTicket.description(),
                0, retailTicket.usageLimit(), Duration.parse(retailTicket.timeLimit()), retailTicket.isActive(), Instant.now(), retailTicket.category()
        );
    }

    private static @NotNull RetailTicketResponseModel toResponseModel(RetailTicketDto dto) {
        return new RetailTicketResponseModel(dto.id().toString(),
                dto.name(),
                dto.description(),
                0L, dto.usageLimit(), dto.timeLimit().toString(), dto.isActive(), dto.createdAt().toString(), dto.category()
        );
    }

}
