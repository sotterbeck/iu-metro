package de.sotterbeck.iumetro.infra.papermc.faregate;

import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.app.faregate.PlayerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Optional;
import java.util.UUID;

public final class SpigotPlayerAdapter implements PlayerAdapter {

    @Override
    public Optional<PositionDto> findPosition(UUID playerId) {
        var player = Bukkit.getPlayer(playerId);
        if (player == null) {
            return Optional.empty();
        }

        var location = player.getLocation();
        return Optional.of(new PositionDto(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }

    @Override
    public void teleport(UUID playerId, PositionDto position) {
        var player = Bukkit.getPlayer(playerId);
        if (player == null) {
            return;
        }

        var world = player.getWorld();
        var oldLocation = player.getLocation();
        var location = new Location(world,
                position.x() + 0.5,
                position.y(),
                position.z() + 0.5,
                oldLocation.getYaw(),
                oldLocation.getPitch()
        );
        player.teleport(location);
    }

}
