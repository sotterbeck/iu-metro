package de.sotterbeck.iumetro.app.faregate;

import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.domain.common.Orientation;
import de.sotterbeck.iumetro.domain.common.Position;
import de.sotterbeck.iumetro.domain.reader.FareGates;

public class FareGateProtectionService {

    private final FareGateSignRepository fareGateSignRepository;

    public FareGateProtectionService(FareGateSignRepository fareGateSignRepository) {
        this.fareGateSignRepository = fareGateSignRepository;
    }

    public boolean canOpenGate(Request request) {
        Position gate = new Position(request.x(), request.y(), request.z());
        Position offset = FareGates.GATE_OFFSET_FROM_SIGN;
        Orientation orientation = Orientation.fromString(request.orientation());

        Position relativeSignPosition = orientation.getRelativePosition(offset);
        Position absoluteSignPostion = gate.translate(relativeSignPosition);

        PositionDto signLocation = new PositionDto(absoluteSignPostion.x(), absoluteSignPostion.y(), absoluteSignPostion.z());
        return fareGateSignRepository.findAt(signLocation).isEmpty();
    }

    public record Request(int x, int y, int z, String orientation) {

    }

}
