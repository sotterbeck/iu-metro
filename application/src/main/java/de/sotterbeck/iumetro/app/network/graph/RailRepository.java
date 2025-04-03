package de.sotterbeck.iumetro.app.network.graph;

import de.sotterbeck.iumetro.app.common.PositionDto;

public interface RailRepository {

    /**
     * Finds the rail shape at the given position.
     *
     * @param position the position to check.
     * @return the rail shape at the given position or NONE if no rail is present.
     */
    RailShape findRailAt(PositionDto position);

    enum RailShape {
        ASCENDING_NORTH,
        ASCENDING_EAST,
        ASCENDING_SOUTH,
        ASCENDING_WEST,
        EAST_WEST,
        NORTH_EAST,
        NORTH_SOUTH,
        NORTH_WEST,
        SOUTH_EAST,
        SOUTH_WEST,
        NONE,
    }

}
