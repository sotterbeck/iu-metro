package de.sotterbeck.iumetro.infra.papermc.faregate;

import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.app.faregate.GateDto;
import de.sotterbeck.iumetro.app.faregate.GateRepository;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Gate;

import java.util.Optional;

public class SpigotGateRepository implements GateRepository {

    private final World world;

    public SpigotGateRepository(World world) {

        this.world = world;
    }

    @Override
    public Optional<GateDto> findAt(PositionDto position) {
        BlockData blockData = world.getBlockData(position.x(), position.y(), position.z());
        if (!(blockData instanceof Gate gate)) {
            return Optional.empty();
        }
        GateDto gateDto = new GateDto(position, gate.getFacing().toString(), gate.isOpen());
        return Optional.of(gateDto);
    }

}
