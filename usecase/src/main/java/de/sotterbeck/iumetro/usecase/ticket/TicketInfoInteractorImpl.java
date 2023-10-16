package de.sotterbeck.iumetro.usecase.ticket;

import de.sotterbeck.iumetro.usecase.barrier.UsageResponseModel;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
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

    @Override
    public List<UsageResponseModel> usages(UUID id) {
        return ticketDsGateway.getTicketUsages(id).stream()
                .map(dsModel -> new UsageResponseModel(
                        dsModel.station(),
                        dsModel.timeAtUsage().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)),
                        dsModel.usageType().toString().toLowerCase()))
                .toList();
    }

}
