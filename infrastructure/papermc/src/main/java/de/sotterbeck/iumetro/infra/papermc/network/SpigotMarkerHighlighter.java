package de.sotterbeck.iumetro.infra.papermc.network;

import de.sotterbeck.iumetro.app.network.graph.MarkerDto;
import de.sotterbeck.iumetro.app.network.graph.MarkerHighlighter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;

public class SpigotMarkerHighlighter implements MarkerHighlighter {

    private final JavaPlugin plugin;
    private final World world;

    public SpigotMarkerHighlighter(JavaPlugin plugin, World world) {
        this.plugin = plugin;
        this.world = world;
    }

    @Override
    public void highlight(Collection<MarkerDto> markers) {
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            var highlightedMarkers = new ArrayList<Entity>();

            for (MarkerDto marker : markers) {
                var loc = new Location(world, marker.position().x(), marker.position().y(), marker.position().z());
                var block = world.getBlockAt(loc);

                var blockDisplay = world.spawn(loc, BlockDisplay.class, entity -> {
                    entity.setBlock(block.getBlockData());
                    entity.setGlowing(true);
                    entity.setPersistent(false);
                });

                highlightedMarkers.add(blockDisplay);
            }

            new BukkitRunnable() {

                @Override
                public void run() {
                    for (Entity entity : highlightedMarkers) {
                        entity.remove();
                    }
                }
            }.runTaskLater(plugin, 200L);
        });
    }

}
