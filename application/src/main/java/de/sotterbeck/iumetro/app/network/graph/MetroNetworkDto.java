package de.sotterbeck.iumetro.app.network.graph;

import de.sotterbeck.iumetro.app.station.MetroStationDto;

import java.util.List;

public record MetroNetworkDto(
        List<MetroStationDto> metroStations,
        List<ConnectionDto> connections
) {

}
