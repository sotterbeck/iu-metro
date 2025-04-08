package de.sotterbeck.iumetro.app.network.graph;

import de.sotterbeck.iumetro.app.network.line.LineDto;

import java.util.List;
import java.util.Map;

public interface MetroNetworkRepository {

    MetroNetworkDto getEntireNetwork();

    void saveNetwork(Map<String, StationNodeDto> graph);

    void saveLine(String name, int color);

    void removeLineByName(String name);

    boolean existsLineByName(String name);

    List<LineDto> getAllLines();

}
