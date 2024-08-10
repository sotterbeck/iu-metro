package de.sotterbeck.iumetro.entity.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PositionTest {

    @Test
    void translate_ShouldTranslatePosition() {
        Position position = new Position(0, 0, 0);

        Position result = position.translate(1, 2, 3);

        assertThat(result).isEqualTo(new Position(1, 2, 3));
    }

}