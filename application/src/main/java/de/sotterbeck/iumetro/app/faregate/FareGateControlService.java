package de.sotterbeck.iumetro.app.faregate;

import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.domain.common.Orientation;
import de.sotterbeck.iumetro.domain.common.Position;
import de.sotterbeck.iumetro.domain.reader.FareGates;

public class FareGateControlService {

    private final GateRepository gateRepository;
    private final GateControlAdapter gateControlAdapter;

    public FareGateControlService(GateRepository gateRepository, GateControlAdapter gateControlAdapter) {
        this.gateRepository = gateRepository;
        this.gateControlAdapter = gateControlAdapter;
    }

    public void openGate(FareGateControlRequestModel request) {
        PositionDto signPosition = request.signPosition();
        Position sign = new Position(signPosition.x(), signPosition.y(), signPosition.z());
        Position offset = FareGates.GATE_OFFSET_FROM_SIGN.multiplied(-1);
        Orientation signOrientation = Orientation.fromString(request.signOrientation()).opposite();

        Position relativeGatePosition = signOrientation.getRelativePosition(offset);
        Position absoluteGatePosition = sign.translate(relativeGatePosition);

        PositionDto gateLocation = new PositionDto(absoluteGatePosition.x(), absoluteGatePosition.y(), absoluteGatePosition.z());
        gateRepository.findAt(gateLocation).ifPresent(gate -> gateControlAdapter.openGate(gate.position()));

    }

}
