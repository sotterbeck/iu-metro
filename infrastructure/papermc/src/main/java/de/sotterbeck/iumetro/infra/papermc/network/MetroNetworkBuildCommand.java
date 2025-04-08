package de.sotterbeck.iumetro.infra.papermc.network;

import de.sotterbeck.iumetro.app.network.graph.StationGraphBuilderService;
import de.sotterbeck.iumetro.infra.papermc.common.CloudAnnotated;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

public class MetroNetworkBuildCommand implements CloudAnnotated {

    private final StationGraphBuilderService metroStationService;

    public MetroNetworkBuildCommand(StationGraphBuilderService metroStationService) {
        this.metroStationService = metroStationService;
    }

    @Command("metro network build")
    @Permission("iumetro.metronetwork.build")
    void metroNetworkBuild(CommandSender sender) {
        sender.sendRichMessage("<green>Metro network build started.");

        var response = metroStationService.discoverConnections();

        switch (response) {
            case StationGraphBuilderService.Response.Failure failure
                    when failure == StationGraphBuilderService.Response.Failure.MARKERS_MISSING ->
                    sender.sendRichMessage("<red>Markers are missing for some stations. Add markers to all stations and try again.");
            case StationGraphBuilderService.Response.Success(var graph) ->
                    sender.sendRichMessage("<green>Metro network build finished. Added " + graph.size() + " stations.");
            default -> throw new IllegalStateException("Unexpected value: " + response);
        }
    }

}
