package de.sotterbeck.iumetro.app.faregate;

import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.app.ticket.DomainTicketFactory;
import de.sotterbeck.iumetro.app.ticket.TicketItemRepository;
import de.sotterbeck.iumetro.app.ticket.TicketRepository;
import de.sotterbeck.iumetro.domain.common.Orientation;
import de.sotterbeck.iumetro.domain.common.Position;
import de.sotterbeck.iumetro.domain.faregate.FareGate;
import de.sotterbeck.iumetro.domain.ticket.TicketUsage;
import de.sotterbeck.iumetro.domain.ticket.UsageType;
import de.sotterbeck.iumetro.domain.ticket.ValidationContext;
import de.sotterbeck.iumetro.domain.ticket.ValidationResult;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class FareGateControlService {

    private final GateRepository gateRepository;
    private final GateControlAdapter gateControlAdapter;
    private final TicketRepository ticketRepository;
    private final TicketItemRepository ticketItemRepository;
    private final PlayerAdapter playerAdapter;

    private final DomainTicketFactory ticketFactory;

    public FareGateControlService(GateRepository gateRepository,
                                  GateControlAdapter gateControlAdapter,
                                  TicketRepository ticketRepository,
                                  TicketItemRepository ticketItemRepository,
                                  PlayerAdapter playerAdapter) {
        this(gateRepository, gateControlAdapter, ticketRepository, ticketItemRepository, playerAdapter, new DomainTicketFactory());
    }

    FareGateControlService(GateRepository gateRepository,
                           GateControlAdapter gateControlAdapter,
                           TicketRepository ticketRepository,
                           TicketItemRepository ticketItemRepository,
                           PlayerAdapter playerAdapter,
                           DomainTicketFactory ticketFactory) {
        this.gateRepository = gateRepository;
        this.gateControlAdapter = gateControlAdapter;
        this.ticketRepository = ticketRepository;
        this.ticketItemRepository = ticketItemRepository;
        this.playerAdapter = Objects.requireNonNull(playerAdapter);
        this.ticketFactory = Objects.requireNonNull(ticketFactory);
    }

    public ResponseModel controlGate(FareGateControlRequestModel request) {
        var ticketId = ticketItemRepository.findCurrentTicket(request.usage().playerId());
        if (ticketId.isEmpty() || !ticketRepository.existsById(ticketId.get())) {
            return new ResponseModel("No ticket found.");
        }

        PositionDto positionDto = request.signPosition();
        FareGate fareGate = FareGate.fromSign(
                new Position(positionDto.x(), positionDto.y(), positionDto.z()),
                Orientation.fromString(request.signOrientation())
        );

        var gatePosition = getGatePosition(fareGate);
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
            if (!playerPassedGate(fareGate, request.usage().playerId())) {
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

    private boolean playerPassedGate(FareGate fareGate, UUID playerId) {
        var position = playerAdapter.findPosition(playerId);
        if (position.isEmpty()) {
            return false;
        }

        var positionDto = position.get();
        var playerPos = new Position(positionDto.x(), positionDto.y(), positionDto.z());
        var passageResult = fareGate.evaluatePassage(playerPos);

        passageResult.teleportTarget()
                .map(pos -> new PositionDto(pos.x(), pos.y(), pos.z()))
                .ifPresent(posDto -> playerAdapter.teleport(playerId, posDto));

        return passageResult.teleportTarget().isEmpty() && passageResult.passedGate();
    }

    private Optional<PositionDto> getGatePosition(FareGate fareGate) {
        var pos = fareGate.gatePosition();
        var posDto = new PositionDto(pos.x(), pos.y(), pos.z());
        return gateRepository.findAt(posDto).map(GateDto::position);
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
