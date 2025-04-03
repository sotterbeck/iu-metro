package de.sotterbeck.iumetro.infra.papermc.network;

import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.app.network.graph.RailRepository;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Rail;

public class SpigotRailRepository implements RailRepository {

    private final World world;

    public SpigotRailRepository(World world) {
        this.world = world;
    }

    @Override
    public RailShape findRailAt(PositionDto position) {
        BlockData blockData = world.getBlockData(position.x(), position.y(), position.z());

        if (!(blockData instanceof Rail)) {
            return RailShape.NONE;
        }

        return toDto(((Rail) blockData).getShape());
    }

    private RailShape toDto(Rail.Shape railShape) {
        return switch (railShape) {
            case NORTH_SOUTH -> RailShape.NORTH_SOUTH;
            case EAST_WEST -> RailShape.EAST_WEST;
            case ASCENDING_EAST -> RailShape.ASCENDING_EAST;
            case ASCENDING_WEST -> RailShape.ASCENDING_WEST;
            case ASCENDING_NORTH -> RailShape.ASCENDING_NORTH;
            case ASCENDING_SOUTH -> RailShape.ASCENDING_SOUTH;
            case SOUTH_EAST -> RailShape.SOUTH_EAST;
            case SOUTH_WEST -> RailShape.SOUTH_WEST;
            case NORTH_WEST -> RailShape.NORTH_WEST;
            case NORTH_EAST -> RailShape.NORTH_EAST;
        };
    }

}
