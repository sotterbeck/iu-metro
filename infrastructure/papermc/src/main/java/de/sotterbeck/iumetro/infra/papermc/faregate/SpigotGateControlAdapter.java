package de.sotterbeck.iumetro.infra.papermc.faregate;

import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.app.faregate.GateControlAdapter;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Gate;
import org.bukkit.plugin.Plugin;

public class SpigotGateControlAdapter implements GateControlAdapter {

    public static final int OPEN_TIME_SECONDS = 2;
    private final World world;
    private final Plugin plugin;

    public SpigotGateControlAdapter(World world, Plugin plugin) {
        this.world = world;
        this.plugin = plugin;
    }

    @Override
    public void openGate(PositionDto positionDto) {
        Block block = world.getBlockAt(positionDto.x(), positionDto.y(), positionDto.z());
        if (!(block.getBlockData() instanceof Gate gate)) {
            System.out.println(block);
            throw new IllegalArgumentException("Block is not a gate.");
        }

        gate.setOpen(true);
        block.setBlockData(gate);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    gate.setOpen(false);
                    block.setBlockData(gate);
                },
                OPEN_TIME_SECONDS * 20);
    }

}
