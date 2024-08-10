package de.sotterbeck.iumetro.usecase.faregate;

import de.sotterbeck.iumetro.entity.reader.TicketReader;
import de.sotterbeck.iumetro.entity.reader.TicketReaderFactory;
import de.sotterbeck.iumetro.entity.reader.TicketReaderFactoryImpl;
import de.sotterbeck.iumetro.entity.reader.TicketReaderInfo;
import de.sotterbeck.iumetro.entity.station.SimpleStation;
import de.sotterbeck.iumetro.entity.ticket.UsageType;
import de.sotterbeck.iumetro.entity.ticket.*;
import de.sotterbeck.iumetro.entity.ticket.constained.ConstrainedTicketBuilder;
import de.sotterbeck.iumetro.usecase.ticket.TicketDto;
import de.sotterbeck.iumetro.usecase.ticket.TicketRepository;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class FareGateValidationInteractor {

    private final TicketRepository ticketRepository;
    private final TicketReaderFactory ticketReaderFactory;
    private final TicketReaderFactory.ReaderType readerType;

    public FareGateValidationInteractor(TicketRepository ticketRepository, BarrierType barrierType) {
        this.ticketRepository = ticketRepository;
        ticketReaderFactory = new TicketReaderFactoryImpl();
        readerType = switch (barrierType) {
            case ENTRY -> TicketReaderFactory.ReaderType.ENTRY;
            case EXIT -> TicketReaderFactory.ReaderType.EXIT;
        };
    }

    public void addUsageToTicket(UUID ticketId, UsageRequestModel usage) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new IllegalArgumentException("Ticket with the ticketId does not exist");
        }
        ticketRepository.saveTicketUsage(ticketId, new UsageDto(usage.station(), usage.timeAtUsage(), usage.usageType()));
    }

    public boolean canOpen(UUID ticketId, UsageRequestModel usage) {
        if (!ticketRepository.existsById(ticketId)) {
            return false;
        }
        TicketReader reader = ticketReaderFactory.create(readerType, new SimpleStation(usage.station()));
        return reader.shouldOpenGate(retrieveTicketEntity(ticketId));
    }

    public boolean canFineUser(UUID ticketId, UsageRequestModel usage) {
        if (!ticketRepository.existsById(ticketId)) {
            return false;
        }
        TicketReader reader = ticketReaderFactory.create(readerType, new SimpleStation(usage.station()));
        return reader.shouldFineUser(retrieveTicketEntity(ticketId));
    }

    private Ticket retrieveTicketEntity(UUID ticketId) {
        TicketDto dsTicket = ticketRepository.get(ticketId).orElseThrow();
        TicketFactory ticketFactory = new SimpleTicketFactory();
        return toTicketEntity(ticketFactory, dsTicket);
    }

    private Ticket toTicketEntity(TicketFactory ticketFactory, TicketDto ticketDto) {
        ConstrainedTicketBuilder ticketBuilder = ticketFactory
                .createConstrainedTicket(ticketDto.name(), ticketDto.id());

        ticketBuilder.usageLimit(ticketDto.usageLimit());
        ticketBuilder.timeLimit(ticketDto.timeLimit(), LocalDateTime.now());

        ticketRepository.getTicketUsages(ticketDto.id()).stream()
                .map(FareGateValidationInteractor::toReaderInfo)
                .forEachOrdered(ticketBuilder::addUsage);

        return ticketBuilder.build();
    }

    @NotNull
    private static TicketReaderInfo toReaderInfo(UsageDto u) {
        UsageType entityUsageType = switch (u.usageType()) {
            case ENTRY -> UsageType.ENTRY;
            case EXIT -> UsageType.EXIT;
        };
        return new ImmutableTicketReaderInfo(new SimpleStation(u.station()),
                u.timeAtUsage().toLocalDateTime(),
                entityUsageType);
    }

    public enum BarrierType {ENTRY, EXIT}

}
