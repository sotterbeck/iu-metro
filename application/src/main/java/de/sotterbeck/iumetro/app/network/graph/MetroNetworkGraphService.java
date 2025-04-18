package de.sotterbeck.iumetro.app.network.graph;

import de.sotterbeck.iumetro.app.network.line.LineResponseModel;
import de.sotterbeck.iumetro.app.station.MetroStationDto;
import de.sotterbeck.iumetro.domain.common.Color;

import java.util.List;

public class MetroNetworkGraphService {

    private final MetroNetworkRepository metroNetworkRepository;

    public MetroNetworkGraphService(MetroNetworkRepository metroNetworkRepository) {
        this.metroNetworkRepository = metroNetworkRepository;
    }

    public Response getEntireNetwork() {
        var network = metroNetworkRepository.getEntireNetwork();

        var nodes = network.metroStations().stream()
                .map(this::toNode)
                .toList();

        var links = network.connections().stream()
                .map(this::toLink)
                .toList();

        return new Response(nodes, links);
    }

    private Response.Node toNode(MetroStationDto station) {
        return new Response.Node(station.id().toString(), station.name(), station.lines().stream()
                .map(line -> {
                    var color = Color.ofValue(line.color());
                    return new LineResponseModel(line.name(), color.hex());
                }).toList()
        );
    }

    private Response.Link toLink(ConnectionDto connection) {
        return new Response.Link(
                connection.from().toString(),
                connection.to().toString(),
                connection.distance()
        );
    }

    public record Response(
            List<Node> nodes,
            List<Link> links
    ) {

        public record Node(String id, String name, List<LineResponseModel> lines) {

        }

        public record Link(String source, String target, int distance) {

        }

    }

}
