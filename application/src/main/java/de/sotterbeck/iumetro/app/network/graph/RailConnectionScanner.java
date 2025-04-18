package de.sotterbeck.iumetro.app.network.graph;

import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.domain.common.Position;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static de.sotterbeck.iumetro.domain.common.Position.Direction.*;

/**
 * RailConnectionScanner is responsible for scanning the rail connections in a given position.
 * It uses the RailRepository to find the rail shape at a given position and determine the possible connections.
 * <p>
 * This class is used to find the neighboring rails that can be connected to the current rail.
 */
public class RailConnectionScanner {

    private record Connection(Position.Direction direction, int heightOffset) {

    }

    /**
     * A map of rail shapes to their possible connections.
     */
    private static final Map<RailRepository.RailShape, List<Connection>> RAIL_CONNECTIONS = Map.of(
            RailRepository.RailShape.ASCENDING_NORTH, List.of(new Connection(SOUTH, 0), new Connection(NORTH, 1), new Connection(SOUTH, -1)),
            RailRepository.RailShape.ASCENDING_EAST, List.of(new Connection(WEST, 0), new Connection(EAST, 1), new Connection(WEST, -1)),
            RailRepository.RailShape.ASCENDING_SOUTH, List.of(new Connection(NORTH, 0), new Connection(SOUTH, 1), new Connection(NORTH, -1)),
            RailRepository.RailShape.ASCENDING_WEST, List.of(new Connection(EAST, 0), new Connection(WEST, 1), new Connection(EAST, -1)),
            RailRepository.RailShape.EAST_WEST, List.of(new Connection(EAST, 0), new Connection(WEST, 0)),
            RailRepository.RailShape.NORTH_EAST, List.of(new Connection(NORTH, 0), new Connection(EAST, 0)),
            RailRepository.RailShape.NORTH_SOUTH, List.of(new Connection(NORTH, 0), new Connection(SOUTH, 0)),
            RailRepository.RailShape.NORTH_WEST, List.of(new Connection(NORTH, 0), new Connection(WEST, 0)),
            RailRepository.RailShape.SOUTH_EAST, List.of(new Connection(SOUTH, 0), new Connection(EAST, 0)),
            RailRepository.RailShape.SOUTH_WEST, List.of(new Connection(SOUTH, 0), new Connection(WEST, 0))
    );

    private final RailRepository railRepository;

    public RailConnectionScanner(RailRepository railRepository) {
        this.railRepository = railRepository;
    }

    /**
     * Gets the connecting rails for a given position.
     *
     * @param position the position to check
     * @return a list of rail positions that are connected to the given position
     */
    public List<PositionDto> getConnectingRails(PositionDto position) {
        var shape = railRepository.findRailAt(position);
        var pos = new Position(position.x(), position.y(), position.z());

        if (shape == RailRepository.RailShape.NONE) {
            return List.of();
        }

        var possibleConnections = RAIL_CONNECTIONS.get(shape);

        return possibleConnections.stream()
                .map(connection -> resolveConnection(pos, connection))
                .filter(Objects::nonNull)
                .distinct()
                .map(this::toPosDto)
                .toList();
    }

    /**
     * Resolves the connection for a given position and connection.
     * A rail connects to another rail if the rail points to any rail.
     * This is the exact same behavior Minecarts traveling over rails.
     * <p>
     * If there were a check, whether the rail connects back to the original rail, the behavior would be different.
     *
     * @param from       the starting position
     * @param connection the connection to resolve
     * @return the target position if a valid connection is found, null otherwise
     */
    private Position resolveConnection(Position from, Connection connection) {
        var targetPos = from.translate(connection.direction(), 1).translate(0, connection.heightOffset(), 0);
        var targetShape = railRepository.findRailAt(toPosDto(targetPos));

        if (targetShape != RailRepository.RailShape.NONE) {
            return targetPos;
        }

        return resolveDescendingConnection(targetPos, connection);
    }

    private Position resolveDescendingConnection(Position targetPos, Connection connection) {
        var dir = connection.direction();
        var lower = targetPos.translate(0, -1, 0);
        var lowerShape = railRepository.findRailAt(toPosDto(lower));

        if (!isAscendingRail(lowerShape)) {
            return null;
        }

        var ascendingDir = RAIL_CONNECTIONS.get(lowerShape).getFirst().direction();
        return (ascendingDir == dir) ? lower : null;
    }

    private boolean isAscendingRail(RailRepository.RailShape shape) {
        return shape == RailRepository.RailShape.ASCENDING_NORTH ||
                shape == RailRepository.RailShape.ASCENDING_EAST ||
                shape == RailRepository.RailShape.ASCENDING_SOUTH ||
                shape == RailRepository.RailShape.ASCENDING_WEST;
    }

    private PositionDto toPosDto(Position targetPos) {
        return new PositionDto(targetPos.x(), targetPos.y(), targetPos.z());
    }
}
