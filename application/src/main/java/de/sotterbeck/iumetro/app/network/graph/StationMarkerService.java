package de.sotterbeck.iumetro.app.network.graph;

import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.app.station.MetroStationDto;
import de.sotterbeck.iumetro.app.station.MetroStationRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class StationMarkerService {

    private final RailRepository railRepository;
    private final StationMarkerRepository markerRepository;
    private final MetroStationRepository metroStationRepository;
    private final MarkerHighlighter highlighter;

    public StationMarkerService(RailRepository railRepository,
                                StationMarkerRepository markerRepository,
                                MetroStationRepository metroStationRepository,
                                MarkerHighlighter highlighter) {
        this.railRepository = railRepository;
        this.markerRepository = markerRepository;
        this.metroStationRepository = metroStationRepository;
        this.highlighter = highlighter;
    }

    public Response add(String station, PositionDto position) {

        if (markerRepository.existsByPosition(position)) {
            return Response.ALREADY_MARKED;
        }

        var shape = railRepository.findRailAt(position);
        if (shape != RailRepository.RailShape.NORTH_SOUTH && shape != RailRepository.RailShape.EAST_WEST) {
            return Response.INVALID_BLOCK;
        }

        if (!metroStationRepository.existsByName(station)) {
            metroStationRepository.save(new MetroStationDto(UUID.randomUUID(), station));
            addMarkerAndHightlight(station, position);
            return Response.SUCCESS_ADDED_STATION;
        }

        addMarkerAndHightlight(station, position);
        return Response.SUCCESS;
    }

    private void addMarkerAndHightlight(String station, PositionDto position) {
        markerRepository.save(station, position);
        var markers = markerRepository.findAllByStation(station);
        highlighter.highlight(markers);
    }

    public boolean remove(PositionDto position) {
        if (!markerRepository.existsByPosition(position)) {
            return false;
        }
        var station = markerRepository.findByPosition(position).map(MarkerDto::stationName).orElseThrow();


        markerRepository.deleteByPosition(position);
        var markers = markerRepository.findAllByStation(station);
        highlighter.highlight(markers);

        return true;
    }

    public boolean highlightAll(String station) {
        if (!metroStationRepository.existsByName(station)) {
            return false;
        }

        var markers = markerRepository.findAllByStation(station);
        highlighter.highlight(markers);

        return true;
    }

    public Map<String, List<PositionDto>> list() {
        return markerRepository.findAll().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(MarkerDto::position)
                                .toList()));
    }


    public enum Response {
        SUCCESS,
        SUCCESS_ADDED_STATION,
        INVALID_BLOCK,
        ALREADY_MARKED,
    }

}
