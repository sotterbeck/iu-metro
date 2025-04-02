package de.sotterbeck.iumetro.usecase.network.graph;

import de.sotterbeck.iumetro.usecase.network.line.LineDto;
import de.sotterbeck.iumetro.usecase.station.MetroStationDto;

import java.util.List;

public record MetroNetworkDto(
        List<MetroStationDto> metroStations,
        List<ConnectionDto> connections,
        List<LineDto> lines
) {

}
