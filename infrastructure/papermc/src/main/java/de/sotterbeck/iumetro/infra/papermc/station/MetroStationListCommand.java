package de.sotterbeck.iumetro.infra.papermc.station;

import de.sotterbeck.iumetro.app.station.MetroStationResponseModel;
import de.sotterbeck.iumetro.app.station.MetroStationService;
import de.sotterbeck.iumetro.infra.papermc.common.CloudAnnotated;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

public class MetroStationListCommand implements CloudAnnotated {

    private final MetroStationService metroStationService;

    public MetroStationListCommand(MetroStationService metroStationService) {
        this.metroStationService = metroStationService;
    }

    @Command("metrostation list")
    @Permission("iumetro.metrostation.list")
    public void metroStationList(CommandSender sender) {
        List<MetroStationResponseModel> stations = metroStationService.getAll();
        if (stations.isEmpty()) {
            sender.sendRichMessage("<red>There are no metro stations.</red>");
            return;
        }

        sender.sendRichMessage("Stations (" + stations.size() + "):");
        for (MetroStationResponseModel station : stations) {
            String position = station.position().map(pos -> "at " + pos).orElse("");
            sender.sendRichMessage("- " + station.name() + " (" + station.displayId() + ") " + position);
        }

    }

}
