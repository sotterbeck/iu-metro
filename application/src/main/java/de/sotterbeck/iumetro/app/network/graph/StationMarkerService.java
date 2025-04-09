package de.sotterbeck.iumetro.app.network.graph;

import de.sotterbeck.iumetro.app.common.PositionDto;
import de.sotterbeck.iumetro.app.station.MetroStationDto;
import de.sotterbeck.iumetro.app.station.MetroStationRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class StationMarkerService {

    private final Supplier<RailRepository> railRepositoryFactory;
    private final StationMarkerRepository markerRepository;
    private final MetroStationRepository metroStationRepository;
    private final MarkerHighlighter highlighter;

    public StationMarkerService(Supplier<RailRepository> railRepositoryFactory,
                                StationMarkerRepository markerRepository,
                                MetroStationRepository metroStationRepository,
                                MarkerHighlighter highlighter) {
        this.railRepositoryFactory = railRepositoryFactory;
        this.markerRepository = markerRepository;
        this.metroStationRepository = metroStationRepository;
        this.highlighter = highlighter;
    }

    public Response add(String station, PositionDto position) {
        if (markerRepository.existsByPosition(position)) {
            return Response.ALREADY_MARKED;
        }

        var shape = getRailShape(position);

        if (shape != RailRepository.RailShape.NORTH_SOUTH && shape != RailRepository.RailShape.EAST_WEST) {
            return Response.INVALID_BLOCK;
        }

        if (!metroStationRepository.existsByName(station)) {
            metroStationRepository.save(new MetroStationDto(UUID.randomUUID(), station));
            addMarkerAndHighlight(station, position);
            return Response.SUCCESS_ADDED_STATION;
        }

        addMarkerAndHighlight(station, position);
        return Response.SUCCESS;
    }

    private RailRepository.RailShape getRailShape(PositionDto position) {
        RailRepository.RailShape shape;
        try (var railRepository = railRepositoryFactory.get()) {
            shape = railRepository.findRailAt(position);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return shape;
    }

    private void addMarkerAndHighlight(String station, PositionDto position) {
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
