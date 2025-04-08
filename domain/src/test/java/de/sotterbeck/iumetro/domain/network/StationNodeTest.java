package de.sotterbeck.iumetro.domain.network;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class StationNodeTest {

    @Test
    void isConnected_ShouldReturnFalse_WhenNoNeighbors() {
        var station = new StationNode("1", "Station", Collections.emptyMap());

        assertThat(station.isConnected()).isFalse();
    }

}