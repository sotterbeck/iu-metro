package de.sotterbeck.iumetro.usecase.barrier;

import de.sotterbeck.iumetro.entity.ticket.*;
import de.sotterbeck.iumetro.usecase.ticket.TicketDsGateway;
import de.sotterbeck.iumetro.usecase.ticket.TicketDsModel;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class TicketBarrierInteractor {

    private final TicketDsGateway ticketDsGateway;
    private final TicketReaderFactory ticketReaderFactory;

    public TicketBarrierInteractor(TicketDsGateway ticketDsGateway, BarrierType entryBarrier) {
        this.ticketDsGateway = ticketDsGateway;
        ticketReaderFactory = switch (entryBarrier) {
            case ENTRY_BARRIER -> new TicketEntryReaderFactory();
            case EXIT_BARRIER -> new TicketExitReaderFactory();
        };
    }

    public void addUsageToTicket(UUID ticketId, UsageRequestModel usage) {
        if (!ticketDsGateway.existsById(ticketId)) {
            throw new IllegalArgumentException("Ticket with the ticketId does not exist");
        }
        ticketDsGateway.addTicketUsage(ticketId, new UsageDsModel(usage.station(), usage.timeAtUsage(), usage.usageType()));
    }

    public boolean canOpen(UUID ticketId, UsageRequestModel usage) {
        if (!ticketDsGateway.existsById(ticketId)) {
            return false;
        }
        TicketDsModel ticketDsModel = ticketDsGateway.get(ticketId).orElseThrow();

        TicketFactory ticketFactory = new SimpleTicketFactory();

        Ticket ticket = toTicketEntity(ticketFactory, ticketDsModel);

        TicketReader reader = ticketReaderFactory.create(new SimpleStation(usage.station()));
        return reader.shouldOpenGate(ticket);
    }

    private Ticket toTicketEntity(TicketFactory ticketFactory, TicketDsModel ticketDsModel) {
        ConstrainedTicketBuilder ticketBuilder = ticketFactory.createConstrainedTicket(ticketDsModel.name(), ticketDsModel.id());

        ticketBuilder.usageLimit(ticketDsModel.usageLimit());
        ticketBuilder.timeLimit(ticketDsModel.timeLimit(), LocalDateTime.now());

        ticketDsGateway.getTicketUsages(ticketDsModel.id()).stream()
                .map(TicketBarrierInteractor::toReaderInfo)
                .forEachOrdered(ticketBuilder::addUsage);

        return ticketBuilder.build();
    }

    @NotNull
    private static TicketReaderInfo toReaderInfo(UsageDsModel u) {
        return new ImmutableTicketReaderInfo(new SimpleStation(u.station()), u.timeAtUsage().toLocalDateTime(), switch (u.usageType()) {
            case ENTRY -> UsageType.ENTRY;
            case EXIT -> UsageType.EXIT;
        });
    }

    public enum BarrierType {ENTRY_BARRIER, EXIT_BARRIER}

}
