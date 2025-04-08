package de.sotterbeck.iumetro.app.network.graph;

import de.sotterbeck.iumetro.app.common.PositionDto;

import java.util.*;
import java.util.stream.Collectors;

public class BFSStationGraphBuilder implements StationGraphBuilder {

    private final RailConnectionScanner railConnectionScanner;

    public BFSStationGraphBuilder(RailConnectionScanner railConnectionScanner) {
        this.railConnectionScanner = railConnectionScanner;
    }

    @Override
    public Map<String, StationNodeDto> buildGraph(Map<String, List<MarkerDto>> markers) {
        System.out.println("Building graph using BFS algorithm");
        var graph = new HashMap<String, StationNodeDto>();
        Map<PositionDto, String> targets = markers.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .map(marker -> new AbstractMap.SimpleEntry<>(marker.position(), entry.getKey())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


        for (var entry : markers.entrySet()) {
            var name = entry.getKey();
            var markerPositions = entry.getValue().stream()
                    .map(MarkerDto::position)
                    .toList();

            System.out.println("Running BFS for station: " + name);
            var distances = bfs(markerPositions, targets);

            graph.put(entry.getKey(), new StationNodeDto(name, distances));
        }

        System.out.println("Finished building graph");
        return graph;
    }

    private Map<String, Integer> bfs(Iterable<PositionDto> sources, Map<PositionDto, String> targets) {
        Queue<Node> queue = new LinkedList<>();
        Set<PositionDto> visited = new HashSet<>();

        Map<String, Integer> distances = new HashMap<>();

        for (var marker : sources) {
            queue.offer(new Node(marker, 0));
            visited.add(marker);
        }

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            var neighbors = railConnectionScanner.getConnectingRails(current.position());
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

    private record Node(PositionDto position, int distance) {

    }

}
