package de.sotterbeck.iumetro.app.network.graph;

import java.util.Map;

public class StationGraphBuilderService {

    private final StationGraphBuilder graphBuilder;
    private final StationMarkerRepository markerRepository;
    private final MetroNetworkRepository metroNetworkRepository;

    public StationGraphBuilderService(StationGraphBuilder graphBuilder,
                                      StationMarkerRepository markerRepository,
                                      MetroNetworkRepository metroNetworkRepository) {
        this.graphBuilder = graphBuilder;
        this.markerRepository = markerRepository;
        this.metroNetworkRepository = metroNetworkRepository;
    }

    public Response discoverConnections() {
        var markers = markerRepository.findAll();

        if (!markerRepository.existsForAllStations()) {
            return Response.Failure.MARKERS_MISSING;
        }

        var graph = graphBuilder.buildGraph(markers);
        metroNetworkRepository.saveNetwork(graph);

        return new Response.Success(graph);
    }

    public sealed interface Response {

        record Success(Map<String, StationNodeDto> graph) implements Response {

        }

        enum Failure implements Response {
            MARKERS_MISSING
        }

    }

}
