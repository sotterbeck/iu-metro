package de.sotterbeck.iumetro.entrypoint.papermc.station;

import de.sotterbeck.iumetro.entrypoint.papermc.common.CloudAnnotated;
import de.sotterbeck.iumetro.usecase.station.MetroStationManagingInteractor;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

public class MetroStationDeleteCommand implements CloudAnnotated {

    private final MetroStationManagingInteractor metroStationManagingInteractor;

    public MetroStationDeleteCommand(MetroStationManagingInteractor metroStationManagingInteractor) {
        this.metroStationManagingInteractor = metroStationManagingInteractor;
    }

    @Command("metrostation delete <station>")
    @Permission("iumetro.metrostation.delete")
    public void metroStationDelete(
            CommandSender sender,
            @Argument(value = "station", suggestions = "stationNames") @Greedy String station
    ) {
        boolean deleted = metroStationManagingInteractor.delete(station);

        if (!deleted) {
            sender.sendRichMessage("<red>Could not delete " + station + ".");
            return;
        }
        sender.sendRichMessage("<green>Successfully deleted " + station + ".");

    }

}
