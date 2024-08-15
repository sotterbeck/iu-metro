package de.sotterbeck.iumetro.entrypoint.papermc.faregate;

import de.sotterbeck.iumetro.entrypoint.papermc.common.CloudAnnotated;
import de.sotterbeck.iumetro.entrypoint.papermc.faregate.sign.FareGateSignItemCreator;
import de.sotterbeck.iumetro.usecase.station.MetroStationManagingInteractor;
import de.sotterbeck.iumetro.usecase.station.MetroStationRequestModel;
import de.sotterbeck.iumetro.usecase.station.MetroStationResponseModel;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;

import java.util.List;

public class FareGateCreateCommand implements CloudAnnotated {

    private final FareGateSignItemCreator signItemCreator;
    private final MetroStationManagingInteractor metroStationManagingInteractor;

    public FareGateCreateCommand(MetroStationManagingInteractor metroStationManagingInteractor, FareGateSignItemCreator signItemCreator) {
        this.signItemCreator = signItemCreator;
        this.metroStationManagingInteractor = metroStationManagingInteractor;
    }

    @Command("faregate create <type> <station>")
    @Permission("iumetro.faregate.create")
    public void fareGateCreate(
            CommandSender sender,
            @Argument(value = "type") FareGateType type,
            @Argument(value = "station", suggestions = "stationNames") @Greedy String stationName
    ) {
        if (!(sender instanceof Player player)) {
            sender.sendRichMessage("<red>You must be a player to use this command.");
            return;
        }

        MetroStationRequestModel station = new MetroStationRequestModel(stationName);
        MetroStationResponseModel savedStation = metroStationManagingInteractor.save(station);

        ItemStack item = signItemCreator.createItem(type.name(), savedStation.name(), savedStation.id(), savedStation.displayId());

        player.getInventory().addItem(item);
        player.sendRichMessage("<green>Created " + type.name() + " sign for station " + stationName + ".");
    }

    enum FareGateType {
        ENTRY, EXIT
    }

    @Suggestions("stationNames")
    public List<String> suggestions() {
        return metroStationManagingInteractor.getAllStationNames();
    }
}
