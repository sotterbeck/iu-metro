package de.sotterbeck.iumetro.infra.papermc.station;

import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.app.station.MetroStationModificationService;
import de.sotterbeck.iumetro.app.station.MetroStationService;
import de.sotterbeck.iumetro.infra.papermc.common.CloudAnnotated;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotation.specifier.Quoted;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;

import java.util.List;

public class MetroStationModifyCommand implements CloudAnnotated {

    private final MetroStationService metroStationService;
    private final MetroStationModificationService metroStationModificationService;

    public MetroStationModifyCommand(MetroStationService metroStationService, MetroStationModificationService metroStationModificationService) {
        this.metroStationService = metroStationService;
        this.metroStationModificationService = metroStationModificationService;
    }

    @Command("metrostation modify <station> set alias <alias>")
    @Permission("iumetro.metrostation.modify")
    public void metroStationModifySetAlias(
            CommandSender sender,
            @Argument(value = "station", suggestions = "stationNamesQuoted") @Quoted String station,
            @Argument(value = "alias") String alias // TODO: check valid characters
    ) {
        MetroStationModificationService.Status status = metroStationModificationService.saveAlias(station, alias);
        switch (status) {
            case NOT_FOUND -> sender.sendRichMessage("<red>Metro station " + station + " not found.");
            case ALREADY_EXISTS -> sender.sendRichMessage("<red>Alias '" + alias + "' already exists.");
            case SUCCESS -> sender.sendRichMessage("<green>Alias " + alias + " has been applied to " + station + ".");
        }
    }

    @Command("metrostation modify <station> set position <position>")
    @Permission("iumetro.metrostation.modify")
    public void metroStationModifySetPosition(
            CommandSender sender,
            @Argument(value = "station", suggestions = "stationNamesQuoted") @Quoted String station,
            @Argument(value = "position") Location position
    ) {
        PositionDto positionDto = new PositionDto(position.getBlockX(), position.getBlockY(), position.getBlockZ());

        MetroStationModificationService.Status status = metroStationModificationService.savePosition(station, positionDto);
        if (status == MetroStationModificationService.Status.NOT_FOUND) {
            sender.sendRichMessage("<red>Metro station " + station + " not found.");
            return;
        }

        sender.sendRichMessage("<green>Set position for station " + station + " to " + positionDto + ".");
    }

    @Command("metrostation modify <station> delete <property>")
    @Permission("iumetro.metrostation.modify")
    public void metroStationModifyDelete(
            CommandSender sender,
            @Argument(value = "station", suggestions = "stationNamesQuoted") @Quoted String station,
            @Argument(value = "property") MetroStationProperty property
    ) {
        MetroStationModificationService.Status status = switch (property) {
            case ALIAS -> metroStationModificationService.deleteAlias(station);
            case POSITION -> metroStationModificationService.deletePosition(station);
        };

        if (status == MetroStationModificationService.Status.NOT_FOUND) {
            sender.sendRichMessage("<red>Metro station " + station + " not found.");
            return;
        }

        String propertyString = property.toString().toLowerCase();
        sender.sendRichMessage("<green>Deleted property " + propertyString + " for station " + station + ".");
    }

    enum MetroStationProperty {
        ALIAS,
        POSITION
    }

    @Suggestions("stationNamesQuoted")
    public List<String> suggestions() {
        return metroStationService.getAllStationNames().stream()
                .map(s -> s.contains(" ") ? "\"%s\"".formatted(s) : s)
                .toList();
    }

}
