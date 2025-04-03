package de.sotterbeck.iumetro.domain.network;

import de.sotterbeck.iumetro.domain.common.Position;

import java.util.Optional;

public record StationNode(String id, String name, boolean hasConnections, Optional<Position> position) {

    public String group() {
        boolean hasPosition = position.isPresent();

        if (hasConnections && hasPosition) {
            return Group.CONNECTED;
        }

        if (!hasPosition && hasConnections) {
            return Group.CONNECTED_NO_POSITION;
        }

        if (!hasPosition) {
            return Group.NO_POSITION;
        }

        return Group.UNCONNECTED;
    }

    public static final class Group {

        private Group() {
        }

        public static final String CONNECTED = "connected";
        public static final String CONNECTED_NO_POSITION = "connected-no-position";
        public static final String NO_POSITION = "no-position";
        public static final String UNCONNECTED = "unconnected";

    }

}
