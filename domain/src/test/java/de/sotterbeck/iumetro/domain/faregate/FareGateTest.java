package de.sotterbeck.iumetro.domain.faregate;

import de.sotterbeck.iumetro.domain.common.Orientation;
import de.sotterbeck.iumetro.domain.common.Position;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FareGateTest {

    @Test
    void gatePosition_ShouldBeCalculatedFromSignPosition() {
        FareGate fareGate = FareGate.fromSign(new Position(166, 71, 149), Orientation.EAST);

        assertThat(fareGate.gatePosition()).isEqualTo(new Position(165, 70, 148));
    }

    @Test
    void fromGate_ShouldCreateSignPositionAndOrientation() {
        FareGate fareGate = FareGate.fromGate(new Position(165, 70, 148), Orientation.WEST);

        assertThat(fareGate.signPosition()).isEqualTo(new Position(166, 71, 149));
        assertThat(fareGate.signOrientation()).isEqualTo(Orientation.EAST);
    }

    @Test
    void fromSignAndFromGate_ShouldBeConsistentForAllOrientations() {
        Position signPosition = new Position(10, 64, 10);

        for (Orientation signOrientation : Orientation.values()) {
            FareGate fromSign = FareGate.fromSign(signPosition, signOrientation);
            FareGate fromGate = FareGate.fromGate(fromSign.gatePosition(), signOrientation.opposite());

            assertThat(fromGate.signPosition()).isEqualTo(signPosition);
            assertThat(fromGate.signOrientation()).isEqualTo(signOrientation);
            assertThat(fromGate.gatePosition()).isEqualTo(fromSign.gatePosition());
        }
    }

    @Test
    void frontOfGatePosition_ShouldReturnPositionInFrontOfGate() {
        FareGate fareGate = FareGate.fromSign(new Position(166, 71, 149), Orientation.EAST);

        assertThat(fareGate.frontOfGatePosition()).isEqualTo(new Position(166, 70, 148));
    }

    @Test
    void evaluatePassage_ShouldTeleportWhenPlayerIsInsideGate() {
        FareGate fareGate = FareGate.fromSign(new Position(166, 71, 149), Orientation.EAST);

        var result = fareGate.evaluatePassage(fareGate.gatePosition());

        assertThat(result.passedGate()).isFalse();
        assertThat(result.teleportTarget()).contains(new Position(166, 70, 148));
    }

    @Test
    void evaluatePassage_ShouldDetectBehindAndInFrontForAllOrientations() {
        FareGate northGate = FareGate.fromSign(new Position(10, 64, 10), Orientation.NORTH);
        assertPassage(northGate, new Position(northGate.gatePosition().x(), 64, northGate.gatePosition().z() + 1), true);
        assertPassage(northGate, new Position(northGate.gatePosition().x(), 64, northGate.gatePosition().z() - 1), false);

        FareGate eastGate = FareGate.fromSign(new Position(10, 64, 10), Orientation.EAST);
        assertPassage(eastGate, new Position(eastGate.gatePosition().x() - 1, 64, eastGate.gatePosition().z()), true);
        assertPassage(eastGate, new Position(eastGate.gatePosition().x() + 1, 64, eastGate.gatePosition().z()), false);

        FareGate southGate = FareGate.fromSign(new Position(10, 64, 10), Orientation.SOUTH);
        assertPassage(southGate, new Position(southGate.gatePosition().x(), 64, southGate.gatePosition().z() - 1), true);
        assertPassage(southGate, new Position(southGate.gatePosition().x(), 64, southGate.gatePosition().z() + 1), false);

        FareGate westGate = FareGate.fromSign(new Position(10, 64, 10), Orientation.WEST);
        assertPassage(westGate, new Position(westGate.gatePosition().x() + 1, 64, westGate.gatePosition().z()), true);
        assertPassage(westGate, new Position(westGate.gatePosition().x() - 1, 64, westGate.gatePosition().z()), false);
    }

    private void assertPassage(FareGate fareGate, Position playerPosition, boolean expectedPassed) {
        var result = fareGate.evaluatePassage(playerPosition);

        assertThat(result.passedGate()).isEqualTo(expectedPassed);
        assertThat(result.teleportTarget()).isEmpty();
    }

}
