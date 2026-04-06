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
import de.sotterbeck.iumetro.domain.ticket.ValidationResult;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class FareGateControlService {

    private static final Position FRONT_OF_GATE_OFFSET = new Position(0, 0, 1);

    private final GateRepository gateRepository;
    private final GateControlAdapter gateControlAdapter;
    private final TicketRepository ticketRepository;
    private final TicketItemRepository ticketItemRepository;
    private final PlayerRepository playerRepository;

    private final DomainTicketFactory ticketFactory;

    public FareGateControlService(GateRepository gateRepository,
                                  GateControlAdapter gateControlAdapter,
                                  TicketRepository ticketRepository,
                                  TicketItemRepository ticketItemRepository,
                                  PlayerRepository playerRepository) {
        this(gateRepository, gateControlAdapter, ticketRepository, ticketItemRepository, playerRepository, new DomainTicketFactory());
    }

    FareGateControlService(GateRepository gateRepository,
                           GateControlAdapter gateControlAdapter,
                           TicketRepository ticketRepository,
                           TicketItemRepository ticketItemRepository,
                           PlayerRepository playerRepository,
                           DomainTicketFactory ticketFactory) {
        this.gateRepository = gateRepository;
        this.gateControlAdapter = gateControlAdapter;
        this.ticketRepository = ticketRepository;
        this.ticketItemRepository = ticketItemRepository;
        this.playerRepository = Objects.requireNonNull(playerRepository);
        this.ticketFactory = Objects.requireNonNull(ticketFactory);
    }

    public ResponseModel controlGate(FareGateControlRequestModel request) {
        var ticketId = ticketItemRepository.findCurrentTicket(request.usage().playerId());
        if (ticketId.isEmpty() || !ticketRepository.existsById(ticketId.get())) {
            return new ResponseModel("No ticket found.");
        }

        var gatePosition = getGatePosition(request);
        if (gatePosition.isEmpty()) {
            return new ResponseModel("No gate found.");
        }

        if (gateControlAdapter.isGateOpen(gatePosition.get())) {
            return new ResponseModel("The gate is already open.");
        }

        var ticketDto = ticketRepository.get(ticketId.get()).orElseThrow();
        var ticket = ticketFactory.from(ticketDto);
        var usages = ticketRepository.getTicketUsages(ticketDto.id()).stream()
                .map(this::toDomainUsage)
                .toList();

        var result = ticket.validate(new ValidationContext(usages, toDomainUsage(request.usage())));

        if (!result.allowGate()) {
            applyTicketActions(result, request, ticketId.get());
            return new ResponseModel("You are not allowed to enter the gate.");
        }

        gateControlAdapter.openGate(gatePosition.get(), () -> {
            if (!playerPassedGate(request, gatePosition.get())) {
                return;
            }
            applyTicketActions(result, request, ticketId.get());
        });

        return new ResponseModel("Gate opened.");
    }

    private void applyTicketActions(ValidationResult result,
                                    FareGateControlRequestModel request,
                                    UUID ticketId) {
        if (result.recordUsage()) {
            var usage = request.usage();
            ticketRepository.saveTicketUsage(ticketId, new UsageDto(usage.station(), usage.timeAtUsage(), usage.usageType()));
        }

        if (result.removeTicket()) {
            ticketItemRepository.deleteTicket(request.usage().playerId(), ticketId);
            ticketRepository.deleteById(ticketId);
        }
    }

    private boolean playerPassedGate(FareGateControlRequestModel request, PositionDto gatePosition) {
        Optional<PositionDto> position = playerRepository.findPosition(request.usage().playerId());
        if (position.isEmpty()) {
            return false;
        }

        PositionDto playerPosition = position.get();
        if (isInsideGate(gatePosition, playerPosition)) {
            teleportPlayerInFrontOfGate(request, gatePosition);
            return false;
        }

        Orientation signOrientation = Orientation.fromString(request.signOrientation());
        return switch (signOrientation) {
            case NORTH -> playerPosition.z() > gatePosition.z();
            case EAST -> playerPosition.x() < gatePosition.x();
            case SOUTH -> playerPosition.z() < gatePosition.z();
            case WEST -> playerPosition.x() > gatePosition.x();
        };
    }

    private boolean isInsideGate(PositionDto gatePosition, PositionDto playerPosition) {
        return playerPosition.x() == gatePosition.x() && playerPosition.z() == gatePosition.z();
    }

    private void teleportPlayerInFrontOfGate(FareGateControlRequestModel request, PositionDto gatePosition) {
        Position gate = new Position(gatePosition.x(), gatePosition.y(), gatePosition.z());
        Orientation signOrientation = Orientation.fromString(request.signOrientation());
        Position relativeFrontOfGatePosition = signOrientation.getRelativePosition(FRONT_OF_GATE_OFFSET);
        Position frontOfGate = gate.translate(relativeFrontOfGatePosition);

        PositionDto frontPosition = new PositionDto(frontOfGate.x(), frontOfGate.y(), frontOfGate.z());
        playerRepository.teleport(request.usage().playerId(), frontPosition);
    }

    private Optional<PositionDto> getGatePosition(FareGateControlRequestModel request) {
        PositionDto signPosition = request.signPosition();
        Position sign = new Position(signPosition.x(), signPosition.y(), signPosition.z());
        Position offset = FareGates.GATE_OFFSET_FROM_SIGN.multiplied(-1);
        Orientation gateOrientation = Orientation.fromString(request.signOrientation()).opposite();
        Position relativeGatePosition = gateOrientation.getRelativePosition(offset);
        Position absoluteGatePosition = sign.translate(relativeGatePosition);

        PositionDto gateLocation = new PositionDto(absoluteGatePosition.x(), absoluteGatePosition.y(), absoluteGatePosition.z());
        return gateRepository.findAt(gateLocation).map(GateDto::position);
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
