package de.sotterbeck.iumetro.domain.common;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrientationTest {

    @Test
    void fromString_ShouldThrowException_WhenUnknownOrientation() {
        String unknownOrientation = "unknownOrientation";

        assertThrows(IllegalArgumentException.class, () -> Orientation.fromString(unknownOrientation));
    }

    @Test
    void fromString_ShouldReturnOrientation_WhenOrientationExists() {
        List<String> orientations = List.of("north", "south", "east", "west");

        List<Orientation> results = orientations.stream()
                .map(Orientation::fromString)
                .toList();

        assertThat(results.get(0)).isEqualTo(Orientation.NORTH);
        assertThat(results.get(1)).isEqualTo(Orientation.SOUTH);
        assertThat(results.get(2)).isEqualTo(Orientation.EAST);
        assertThat(results.get(3)).isEqualTo(Orientation.WEST);
    }

    @Test
    void getRelativePosition_ShouldReturnCorrectPosition() {
        Orientation west = Orientation.fromString("west");
        Position original = new Position(165, 70, 148);
        Position offset = new Position(1, 1, -1);

        Position result = west.getRelativePosition(offset);
        Position finalResult = original.translate(result);

        assertThat(finalResult).isEqualTo(new Position(166, 71, 147));
    }

}