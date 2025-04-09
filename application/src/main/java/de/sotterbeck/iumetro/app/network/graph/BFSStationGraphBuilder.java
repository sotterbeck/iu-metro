package de.sotterbeck.iumetro.app.network.graph;

import de.sotterbeck.iumetro.app.common.PositionDto;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BFSStationGraphBuilder implements StationGraphBuilder {

    private final Supplier<RailRepository> railRepositoryFactory;

    public BFSStationGraphBuilder(Supplier<RailRepository> railRepositoryFactory) {
        this.railRepositoryFactory = railRepositoryFactory;
    }

    @Override
    public Map<String, StationNodeDto> buildGraph(Map<String, List<MarkerDto>> markers) {
        var graph = new HashMap<String, StationNodeDto>();

        try (var railRepository = railRepositoryFactory.get()) {
            var railConnectionScanner = new RailConnectionScanner(railRepository);

            Map<PositionDto, String> targets = getTargetPositions(markers);

            for (var entry : markers.entrySet()) {
                var name = entry.getKey();
                var markerPositions = entry.getValue().stream()
                        .map(MarkerDto::position)
                        .toList();

                var distances = bfs(railConnectionScanner, markerPositions, targets);

                graph.put(entry.getKey(), new StationNodeDto(name, distances));
            }

        } catch (Exception e) {
            throw new RuntimeException("Error while building graph", e);
        }

        return graph;
    }

    private Map<String, Integer> bfs(RailConnectionScanner railScanner, Iterable<PositionDto> sources, Map<PositionDto, String> targets) {
        Queue<Node> queue = new LinkedList<>();
        Set<PositionDto> visited = new HashSet<>();

        Map<String, Integer> distances = new HashMap<>();

        for (var marker : sources) {
            queue.offer(new Node(marker, 0));
            visited.add(marker);
        }

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            var neighbors = railScanner.getConnectingRails(current.position());
            for (PositionDto neighbor : neighbors) {
                if (visited.contains(neighbor)) {
                    continue;
                }
                visited.add(neighbor);
                var newNode = new Node(neighbor, current.distance() + 1);

                if (targets.containsKey(neighbor) && !distances.containsKey(targets.get(neighbor))) {
                    var neighborStationName = targets.get(neighbor);
                    distances.put(neighborStationName, newNode.distance());
                } else {
                    queue.offer(newNode);
                }
            }
        }
        return distances;
    }

    private Map<PositionDto, String> getTargetPositions(Map<String, List<MarkerDto>> markers) {
        return markers.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .map(marker -> new AbstractMap.SimpleEntry<>(marker.position(), entry.getKey())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private record Node(PositionDto position, int distance) {

    }

}
