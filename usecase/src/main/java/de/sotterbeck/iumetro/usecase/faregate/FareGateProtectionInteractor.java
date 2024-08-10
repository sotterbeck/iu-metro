package de.sotterbeck.iumetro.usecase.faregate;

import de.sotterbeck.iumetro.entity.common.Orientation;
import de.sotterbeck.iumetro.entity.common.Position;

public class FareGateProtectionInteractor {

    private final FareGateSignRepository fareGateSignRepository;

    public FareGateProtectionInteractor(FareGateSignRepository fareGateSignRepository) {
        this.fareGateSignRepository = fareGateSignRepository;
    }

    public boolean canOpenGate(GateRequestModel request) {
        Position gate = new Position(request.x(), request.y(), request.z());
        Position offset = FareGates.GATE_OFFSET_FROM_SIGN;
        Orientation orientation = Orientation.fromString(request.orientation());

        Position relativeSignPosition = orientation.getRelativePosition(offset);
        Position absoluteSignPostion = gate.translate(relativeSignPosition);

        PositionDto signLocation = new PositionDto(absoluteSignPostion.x(), absoluteSignPostion.y(), absoluteSignPostion.z());
        return fareGateSignRepository.findAt(signLocation).isEmpty();
    }

}
