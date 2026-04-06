package de.sotterbeck.iumetro.domain.faregate;

import de.sotterbeck.iumetro.domain.common.Orientation;
import de.sotterbeck.iumetro.domain.common.Position;

import java.util.Objects;
import java.util.Optional;

public final class FareGate {

    private static final Position FRONT_OF_GATE_OFFSET = new Position(0, 0, 1);

    private final Position signPosition;
    private final Orientation signOrientation;

    private FareGate(Position signPosition, Orientation signOrientation) {
        this.signPosition = Objects.requireNonNull(signPosition, "Sign position must not be null.");
        this.signOrientation = Objects.requireNonNull(signOrientation, "Sign orientation must not be null.");
    }

    public static FareGate fromSign(Position signPosition, Orientation signOrientation) {
        return new FareGate(signPosition, signOrientation);
    }

    public static FareGate fromGate(Position gatePosition, Orientation gateOrientation) {
        Objects.requireNonNull(gatePosition, "Gate position must not be null.");
        Objects.requireNonNull(gateOrientation, "Gate orientation must not be null.");

        Position relativeSignPosition = gateOrientation.getRelativePosition(FareGates.GATE_OFFSET_FROM_SIGN);
        Position signPosition = gatePosition.translate(relativeSignPosition);
        Orientation signOrientation = gateOrientation.opposite();
        return new FareGate(signPosition, signOrientation);
    }

    public Position signPosition() {
        return signPosition;
    }

    public Orientation signOrientation() {
        return signOrientation;
    }

    public Position gatePosition() {
        Position offset = FareGates.GATE_OFFSET_FROM_SIGN.multiplied(-1);
        Orientation gateOrientation = signOrientation.opposite();
        Position relativeGatePosition = gateOrientation.getRelativePosition(offset);
        return signPosition.translate(relativeGatePosition);
    }

    public Position frontOfGatePosition() {
        Position gatePosition = gatePosition();
        Position relativeFrontOfGatePosition = signOrientation.getRelativePosition(FRONT_OF_GATE_OFFSET);
        return gatePosition.translate(relativeFrontOfGatePosition);
    }

    public PassageResult evaluatePassage(Position playerPosition) {
        Objects.requireNonNull(playerPosition, "Player position must not be null.");

        Position gatePosition = gatePosition();
        if (isInsideGate(gatePosition, playerPosition)) {
            return PassageResult.teleport(frontOfGatePosition());
        }
        return PassageResult.passed(isBehindGate(gatePosition, playerPosition));
    }

    private static boolean isInsideGate(Position gatePosition, Position playerPosition) {
        return playerPosition.x() == gatePosition.x() && playerPosition.z() == gatePosition.z();
    }

    private boolean isBehindGate(Position gatePosition, Position playerPosition) {
        return switch (signOrientation) {
            case NORTH -> playerPosition.z() > gatePosition.z();
            case EAST -> playerPosition.x() < gatePosition.x();
            case SOUTH -> playerPosition.z() < gatePosition.z();
            case WEST -> playerPosition.x() > gatePosition.x();
        };
    }

    public record PassageResult(boolean passedGate, Optional<Position> teleportTarget) {

        public PassageResult {
            Objects.requireNonNull(teleportTarget, "Teleport target must not be null.");
        }

        public static PassageResult passed(boolean passedGate) {
            return new PassageResult(passedGate, Optional.empty());
        }

        public static PassageResult teleport(Position teleportTarget) {
            return new PassageResult(false, Optional.of(teleportTarget));
        }

    }

}
