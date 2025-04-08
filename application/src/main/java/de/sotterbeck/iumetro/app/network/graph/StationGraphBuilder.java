package de.sotterbeck.iumetro.app.network.graph;

import java.util.List;
import java.util.Map;

public interface StationGraphBuilder {

    Map<String, StationNodeDto> buildGraph(Map<String, List<MarkerDto>> markers);

}
