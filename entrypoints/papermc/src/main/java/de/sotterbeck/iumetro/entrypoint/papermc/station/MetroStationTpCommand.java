package de.sotterbeck.iumetro.entrypoint.papermc.station;

import de.sotterbeck.iumetro.entrypoint.papermc.common.CloudAnnotated;
import de.sotterbeck.iumetro.usecase.faregate.PositionDto;
import de.sotterbeck.iumetro.usecase.station.MetroStationTeleportInteractor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;

import java.util.List;

public class MetroStationTpCommand implements CloudAnnotated {

    private final MetroStationTeleportInteractor metroStationTeleportInteractor;
    private final World world;
    private final Plugin plugin;

    public MetroStationTpCommand(MetroStationTeleportInteractor metroStationTeleportInteractor, Plugin plugin) {
        this.metroStationTeleportInteractor = metroStationTeleportInteractor;
        this.world = plugin.getServer().getWorlds().getFirst();
        this.plugin = plugin;
    }

    @Command("metrostation tp <station>")
    @Permission("iumetro.metrostation.tp")
    public void metroStationTp(
            CommandSender sender,
            @Argument(value = "station", suggestions = "teleportableStationNames") @Greedy String station
    ) {
        if (!(sender instanceof Player player)) {
            sender.sendRichMessage("<red>Only players can use this command!");
            return;
        }
        boolean teleportable = metroStationTeleportInteractor.isTeleportable(station);
        if (!teleportable) {
            player.sendRichMessage("<red>You can't teleport to this station!");
            return;
        }

        PositionDto position = metroStationTeleportInteractor.getPosition(station).orElseThrow();

        Location location = new Location(world, position.x(), position.y(), position.z());
        plugin.getServer().getScheduler().runTask(plugin, () -> player.teleport(location));
        player.sendRichMessage("<green>You have been teleported to station " + station + ".");
    }

    @Suggestions("teleportableStationNames")
    public List<String> suggestions() {
        return metroStationTeleportInteractor.getAllTeleportableStationNames();
    }

}
