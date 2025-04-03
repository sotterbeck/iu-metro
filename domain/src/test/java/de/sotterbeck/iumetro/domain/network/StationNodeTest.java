package de.sotterbeck.iumetro.domain.network;

import de.sotterbeck.iumetro.domain.common.Position;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class StationNodeTest {

    @Test
    void group_ShouldReturnUnconnected_WhenStationHasPositionButNoConnections() {
        Position position = new Position(0, 0, 0);
        StationNode stationNode = new StationNode("1", "Station", false, Optional.of(position));

        String group = stationNode.group();

        assertThat(group).isEqualTo(StationNode.Group.UNCONNECTED);
    }

    @Test
    void group_ShouldReturnNoPosition_WhenStationHasNoPosition() {
        StationNode stationNode = new StationNode("1", "Station", false, Optional.empty());

        String group = stationNode.group();

        assertThat(group).isEqualTo(StationNode.Group.NO_POSITION);
    }

    @Test
    void group_ShouldReturnConnectedNoPosition_WhenStationHasNoPositionButConnections() {
        StationNode stationNode = new StationNode("1", "Station", true, Optional.empty());

        String group = stationNode.group();

        assertThat(group).isEqualTo(StationNode.Group.CONNECTED_NO_POSITION);
    }

    @Test
    void group_ShouldReturnConnected_WhenStationHasPositionAndHasConnections() {
        Position position = new Position(0, 0, 0);
        StationNode stationNode = new StationNode("1", "Station", true, Optional.of(position));

        String group = stationNode.group();

        assertThat(group).isEqualTo(StationNode.Group.CONNECTED);
    }

}