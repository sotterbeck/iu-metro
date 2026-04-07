package de.sotterbeck.iumetro.app.faregate;

import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.domain.common.Orientation;
import de.sotterbeck.iumetro.domain.common.Position;
import de.sotterbeck.iumetro.domain.faregate.FareGate;

public class FareGateProtectionService {

    private final FareGateSignRepository fareGateSignRepository;

    public FareGateProtectionService(FareGateSignRepository fareGateSignRepository) {
        this.fareGateSignRepository = fareGateSignRepository;
    }

    public boolean canOpenGate(Request request) {
        Position gatePosition = new Position(request.x(), request.y(), request.z());
        Orientation gateOrientation = Orientation.fromString(request.orientation());
        FareGate fareGate = FareGate.fromGate(gatePosition, gateOrientation);

        Position signPosition = fareGate.signPosition();
        PositionDto signLocation = new PositionDto(signPosition.x(), signPosition.y(), signPosition.z());
        return fareGateSignRepository.findAt(signLocation).isEmpty();
    }

    public record Request(int x, int y, int z, String orientation) {

    }

}
