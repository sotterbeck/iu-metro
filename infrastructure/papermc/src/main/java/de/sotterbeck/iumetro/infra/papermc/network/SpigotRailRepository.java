package de.sotterbeck.iumetro.infra.papermc.network;

import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.app.network.graph.RailRepository;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Rail;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutionException;

public class SpigotRailRepository implements RailRepository {

    private final JavaPlugin plugin;
    private final World world;

    public SpigotRailRepository(JavaPlugin plugin, World world) {
        this.plugin = plugin;
        this.world = world;
    }

    @Override
    public RailShape findRailAt(PositionDto position) {
        if (Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("RailRepository must only be used off the main thread.");
        }
        Location location = new Location(world, position.x(), position.y(), position.z());
        var blockData = getBlockDataSync(location);

        if (!(blockData instanceof Rail rail)) {
            return RailShape.NONE;
        }

        return toDto(rail.getShape());
    }

    private BlockData getBlockDataSync(Location location) {
        BlockData blockData;
        try {
            blockData = plugin.getServer().getScheduler().callSyncMethod(plugin, () -> world.getBlockData(location)).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return blockData;
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

    @Override
    public void close() throws Exception {
        // Do nothing
    }

}
