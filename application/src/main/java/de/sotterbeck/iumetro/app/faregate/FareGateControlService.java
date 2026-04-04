package de.sotterbeck.iumetro.app.faregate;

import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.app.ticket.DomainTicketFactory;
import de.sotterbeck.iumetro.app.ticket.TicketItemRepository;
import de.sotterbeck.iumetro.app.ticket.TicketRepository;
import de.sotterbeck.iumetro.domain.common.Orientation;
import de.sotterbeck.iumetro.domain.common.Position;
import de.sotterbeck.iumetro.domain.faregate.FareGates;
import de.sotterbeck.iumetro.domain.ticket.TicketUsage;
import de.sotterbeck.iumetro.domain.ticket.UsageType;
import de.sotterbeck.iumetro.domain.ticket.ValidationContext;

import java.util.Objects;

public class FareGateControlService {

    private final GateRepository gateRepository;
    private final GateControlAdapter gateControlAdapter;
    private final TicketRepository ticketRepository;
    private final TicketItemRepository ticketItemRepository;

    private final DomainTicketFactory ticketFactory;

    public FareGateControlService(GateRepository gateRepository,
                                  GateControlAdapter gateControlAdapter,
                                  TicketRepository ticketRepository,
                                  TicketItemRepository ticketItemRepository) {
        this(gateRepository, gateControlAdapter, ticketRepository, ticketItemRepository, new DomainTicketFactory());
    }

    FareGateControlService(GateRepository gateRepository,
                           GateControlAdapter gateControlAdapter,
                           TicketRepository ticketRepository,
                           TicketItemRepository ticketItemRepository,
                           DomainTicketFactory ticketFactory) {
        this.gateRepository = gateRepository;
        this.gateControlAdapter = gateControlAdapter;
        this.ticketRepository = ticketRepository;
        this.ticketItemRepository = ticketItemRepository;
        this.ticketFactory = Objects.requireNonNull(ticketFactory);
    }

    public ResponseModel controlGate(FareGateControlRequestModel request) {
        var ticketId = ticketItemRepository.findCurrentTicket(request.usage().playerId());
        if (ticketId.isEmpty() || !ticketRepository.existsById(ticketId.get())) {
            return new ResponseModel("No ticket found.");
        }
        var ticketDto = ticketRepository.get(ticketId.get()).orElseThrow();
        var ticket = ticketFactory.from(ticketDto);

        var usages = ticketRepository.getTicketUsages(ticketDto.id()).stream()
                .map(this::toDomainUsage)
                .toList();

        var result = ticket.validate(new ValidationContext(usages, toDomainUsage(request.usage())));


        if (result.allowGate()) {
            open(request);
        }

        if (result.recordUsage()) {
            var usage = request.usage();
            ticketRepository.saveTicketUsage(ticketId.get(), new UsageDto(usage.station(), usage.timeAtUsage(), usage.usageType()));
        }

        if (result.removeTicket()) {
            ticketItemRepository.deleteTicket(request.usage().playerId(), ticketId.get());
            ticketRepository.deleteById(ticketId.get());
        }

        return new ResponseModel("Gate opened.");
    }

    private void open(FareGateControlRequestModel request) {
        PositionDto signPosition = request.signPosition();
        Position sign = new Position(signPosition.x(), signPosition.y(), signPosition.z());
        Position offset = FareGates.GATE_OFFSET_FROM_SIGN.multiplied(-1);
        Orientation signOrientation = Orientation.fromString(request.signOrientation()).opposite();

        Position relativeGatePosition = signOrientation.getRelativePosition(offset);
        Position absoluteGatePosition = sign.translate(relativeGatePosition);

        PositionDto gateLocation = new PositionDto(absoluteGatePosition.x(), absoluteGatePosition.y(), absoluteGatePosition.z());
        gateRepository.findAt(gateLocation).ifPresent(gate -> gateControlAdapter.openGate(gate.position()));
    }

    private TicketUsage toDomainUsage(UsageDto dto) {
        var type = switch (dto.usageType()) {
            case ENTRY -> UsageType.ENTRY;
            case EXIT -> UsageType.EXIT;
        };
        return new TicketUsage(dto.timeAtUsage(), type);
    }

    private TicketUsage toDomainUsage(UsageRequestModel model) {
        var type = switch (model.usageType()) {
            case ENTRY -> UsageType.ENTRY;
            case EXIT -> UsageType.EXIT;
        };
        return new TicketUsage(model.timeAtUsage(), type);
    }

    public record ResponseModel(String message) {

    }
}
