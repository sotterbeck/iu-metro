package de.sotterbeck.iumetro.infra.papermc.station;

import de.sotterbeck.iumetro.app.station.MetroStationService;
import de.sotterbeck.iumetro.infra.papermc.common.CloudAnnotated;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

public class MetroStationDeleteCommand implements CloudAnnotated {

    private final MetroStationService metroStationService;

    public MetroStationDeleteCommand(MetroStationService metroStationService) {
        this.metroStationService = metroStationService;
    }

    @Command("metro station delete <station>")
    @Permission("iumetro.metrostation.delete")
    public void metroStationDelete(
            CommandSourceStack source,
            @Argument(value = "station", suggestions = "stationNames") @Greedy String station
    ) {
        var sender = source.getSender();
        boolean deleted = metroStationService.delete(station);

        if (!deleted) {
            sender.sendRichMessage("<red>Could not delete " + station + ".");
            return;
        }
        sender.sendRichMessage("<green>Successfully deleted " + station + ".");

    }

}
