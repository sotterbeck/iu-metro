package de.sotterbeck.iumetro.infra.papermc.station;

import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.app.station.MetroStationTeleportService;
import de.sotterbeck.iumetro.infra.papermc.common.CloudAnnotated;
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

    private final MetroStationTeleportService metroStationTeleportService;
    private final World world;
    private final Plugin plugin;

    public MetroStationTpCommand(MetroStationTeleportService metroStationTeleportService, Plugin plugin) {
        this.metroStationTeleportService = metroStationTeleportService;
        this.world = plugin.getServer().getWorlds().getFirst();
        this.plugin = plugin;
    }

    @Command("metro station tp <station>")
    @Permission("iumetro.metrostation.tp")
    public void metroStationTp(
            CommandSender sender,
            @Argument(value = "station", suggestions = "teleportableStationNames") @Greedy String station
    ) {
        if (!(sender instanceof Player player)) {
            sender.sendRichMessage("<red>Only players can use this command!");
            return;
        }
        boolean teleportable = metroStationTeleportService.isTeleportable(station);
        if (!teleportable) {
            player.sendRichMessage("<red>You can't teleport to this station!");
            return;
        }

        PositionDto position = metroStationTeleportService.getPosition(station).orElseThrow();

        Location location = new Location(world, position.x(), position.y(), position.z());
        plugin.getServer().getScheduler().runTask(plugin, () -> player.teleport(location));
        player.sendRichMessage("<green>You have been teleported to station " + station + ".");
    }

    @Suggestions("teleportableStationNames")
    public List<String> suggestions() {
        return metroStationTeleportService.getAllTeleportableStationNames();
    }

}
