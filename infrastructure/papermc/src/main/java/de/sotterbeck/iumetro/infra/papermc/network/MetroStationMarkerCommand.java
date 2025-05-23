package de.sotterbeck.iumetro.infra.papermc.network;

import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.app.network.graph.StationMarkerService;
import de.sotterbeck.iumetro.infra.papermc.common.CloudAnnotated;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

public class MetroStationMarkerCommand implements CloudAnnotated {

    public static final int MAX_DISTANCE = 6;
    private final StationMarkerService stationMarkerService;

    public MetroStationMarkerCommand(StationMarkerService stationMarkerService) {
        this.stationMarkerService = stationMarkerService;
    }

    @Command("metro station marker add <station>")
    @Permission("iumetro.metrostation.marker.add")
    public void metroStationMarkerAdd(
            CommandSender sender,
            @Argument(value = "station", suggestions = "stationNames") @Greedy String station
    ) {
        if (!(sender instanceof Player player)) {
            sender.sendRichMessage("<red>You must be a player to use this command.");
            return;
        }
        var targetBlock = player.getTargetBlockExact(MAX_DISTANCE);
        if (targetBlock == null) {
            sender.sendRichMessage("<red>Look at the rail you want to mark.");
            return;
        }

        var position = new PositionDto(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ());
        var response = stationMarkerService.add(station, position);

        switch (response) {
            case SUCCESS ->
                    player.sendRichMessage("<yellow>Successfully marked " + position + " as a station rail. Showing marked rails for 10 seconds.");
            case SUCCESS_ADDED_STATION ->
                    player.sendRichMessage("<yellow>Successfully marked " + position + " as a station rail and created new station " + station + ". Showing marked rails for 10 seconds.");
            case INVALID_BLOCK ->
                    player.sendRichMessage("<red>Invalid block. Only straight rails can be mark as a station rail.");
            case ALREADY_MARKED -> player.sendRichMessage("<red>That station has been marked.");
        }
    }

    @Command("metro station marker remove")
    @Permission("iumetro.metrostation.marker.remove")
    public void metroStationMarkerRemove(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendRichMessage("<red>You must be a player to use this command.");
            return;
        }

        var targetBlock = player.getTargetBlockExact(MAX_DISTANCE);
        if (targetBlock == null) {
            sender.sendRichMessage("<red>Look at the rail you want to remove the marking.");
            return;
        }

        var position = new PositionDto(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ());
        var response = stationMarkerService.remove(position);

        if (!response) {
            sender.sendRichMessage("<red>Unable to remove marking.");
            return;
        }

        sender.sendRichMessage("<yellow>Successfully removed marking at position " + position + ".");
    }

    @Command("metro station marker show <station>")
    @Permission("iumetro.metrostation.marker.show")
    public void metroStationMarkerShow(
            CommandSender sender,
            @Argument(value = "station", suggestions = "stationNames") @Greedy String station
    ) {
        if (!(sender instanceof Player)) {
            sender.sendRichMessage("<red>You must be a player to use this command.");
            return;
        }

        var response = stationMarkerService.highlightAll(station);
        if (!response) {
            sender.sendRichMessage("<red>Could not find station " + station + ".");
        }

        sender.sendRichMessage("<yellow>Highlighting all markers for station " + station + " for 10 seconds.");
    }

    @Command("metro station marker list")
    @Permission("iumetro.metrostation.marker.list")
    public void metroStationMarkerList(CommandSender sender) {
        var markers = stationMarkerService.list();
        if (markers.isEmpty()) {
            sender.sendRichMessage("<red>There are no markers.");
            return;
        }

        var markerCount = markers.values().stream()
                .mapToInt(List::size)
                .sum();

        sender.sendRichMessage("Markers (%d):".formatted(markerCount));
        for (var entry : markers.entrySet()) {
            String station = entry.getKey();
            var positions = String.join(", ", entry.getValue().stream()
                    .map(PositionDto::toString)
                    .toList());

            sender.sendRichMessage("- %s (%d): %s".formatted(station, entry.getValue().size(), positions));
        }
    }
}
