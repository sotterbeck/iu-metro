package de.sotterbeck.iumetro.usecase.network.graph;

import de.sotterbeck.iumetro.usecase.network.line.LineConnectionDto;
import de.sotterbeck.iumetro.usecase.network.line.LineDto;

import java.util.List;
import java.util.UUID;

public interface MetroNetworkRepository {

    MetroNetworkDto getEntireNetwork();

    void saveLine(String name, int color);

    void removeLineByName(String name);

    boolean existsLineByName(String name);

    List<LineDto> getAllLines();

    void saveConnection(UUID station1Id, UUID station2Id, int distance, List<LineConnectionDto> lines);

    void removeConnection(UUID station1Id, UUID station2Id);

    boolean existsConnection(UUID station1Id, UUID station2Id);

}
