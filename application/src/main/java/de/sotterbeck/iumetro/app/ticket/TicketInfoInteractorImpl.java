package de.sotterbeck.iumetro.app.ticket;

import de.sotterbeck.iumetro.app.faregate.UsageResponseModel;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.UUID;

public class TicketInfoInteractorImpl implements TicketInfoInteractor {

    private final TicketRepository ticketRepository;

    public TicketInfoInteractorImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public boolean exists(String ticketId) {
        UUID id = UUID.fromString(ticketId);
        return ticketRepository.existsById(id);
    }

    @Override
    public List<UsageResponseModel> usages(UUID id) {
        return ticketRepository.getTicketUsages(id).stream()
                .map(dto -> new UsageResponseModel(
                        dto.station(),
                        dto.timeAtUsage().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)),
                        dto.usageType().toString().toLowerCase()))
                .toList();
    }

}
