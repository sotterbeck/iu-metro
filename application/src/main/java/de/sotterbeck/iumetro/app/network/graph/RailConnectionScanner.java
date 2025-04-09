package de.sotterbeck.iumetro.app.network.graph;

import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.domain.common.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * RailConnectionScanner is responsible for scanning the rail connections in a given position.
 * It uses the RailRepository to find the rail shape at a given position and determine the possible connections.
 * <p>
 * This class is used to find the neighboring rails that can be connected to the current rail.
 */
public class RailConnectionScanner {

    /**
     * A map of rail shapes to their possible connections. The keys are the rail shapes, and the values are lists of
     * directions that can be connected to the rail shape.
     */
    private static final Map<RailRepository.RailShape, List<Position.Direction>> RAIL_CONNECTIONS = Map.of(
            RailRepository.RailShape.ASCENDING_NORTH, List.of(Position.Direction.SOUTH),
            RailRepository.RailShape.ASCENDING_EAST, List.of(Position.Direction.WEST),
            RailRepository.RailShape.ASCENDING_SOUTH, List.of(Position.Direction.NORTH),
            RailRepository.RailShape.ASCENDING_WEST, List.of(Position.Direction.WEST),
            RailRepository.RailShape.EAST_WEST, List.of(Position.Direction.EAST, Position.Direction.WEST),
            RailRepository.RailShape.NORTH_EAST, List.of(Position.Direction.NORTH, Position.Direction.EAST),
            RailRepository.RailShape.NORTH_SOUTH, List.of(Position.Direction.NORTH, Position.Direction.SOUTH),
            RailRepository.RailShape.NORTH_WEST, List.of(Position.Direction.NORTH, Position.Direction.WEST),
            RailRepository.RailShape.SOUTH_EAST, List.of(Position.Direction.SOUTH, Position.Direction.EAST),
            RailRepository.RailShape.SOUTH_WEST, List.of(Position.Direction.SOUTH, Position.Direction.WEST)
    );

    private static final Set<RailRepository.RailShape> ASCENDING_RAIL_SHAPES = Set.of(
            RailRepository.RailShape.ASCENDING_NORTH,
            RailRepository.RailShape.ASCENDING_EAST,
            RailRepository.RailShape.ASCENDING_SOUTH,
            RailRepository.RailShape.ASCENDING_WEST
    );

    private final RailRepository railRepository;

    public RailConnectionScanner(RailRepository railRepository) {
        this.railRepository = railRepository;
    }

    public List<PositionDto> getConnectingRails(PositionDto position) {
        List<PositionDto> neighbors = new ArrayList<>();
        var shape = railRepository.findRailAt(position);
        var pos = new Position(position.x(), position.y(), position.z());

        if (shape == RailRepository.RailShape.NONE) {
            return List.of();
        }

        var directions = RAIL_CONNECTIONS.get(shape);

        for (Position.Direction direction : directions) {
            var newPos = pos.translate(direction, 1);
            var newPosDto = new PositionDto(newPos.x(), newPos.y(), newPos.z());
            var newShape = railRepository.findRailAt(newPosDto);

            if (newShape == RailRepository.RailShape.NONE) {
                var lowerPos = newPos.translate(0, -1, 0);
                var lowerPosDto = new PositionDto(lowerPos.x(), lowerPos.y(), lowerPos.z());
                var lowerShape = railRepository.findRailAt(lowerPosDto);

                if (lowerShape == RailRepository.RailShape.NONE || !ASCENDING_RAIL_SHAPES.contains(lowerShape)) {
                    continue;
                }

                // TODO: fix multiple ascending rails UP, they only work downwards.
                var lowerShapeDirection = RAIL_CONNECTIONS.get(lowerShape).getFirst();
                if (lowerShapeDirection != null && lowerShapeDirection == direction) {
                    neighbors.add(lowerPosDto);
                    continue;
                }
            }

            neighbors.add(newPosDto);
        }

        return neighbors;
    }

}
