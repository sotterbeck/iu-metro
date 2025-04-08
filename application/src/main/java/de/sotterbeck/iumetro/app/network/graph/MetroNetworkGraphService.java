package de.sotterbeck.iumetro.app.network.graph;

import java.util.List;

public class MetroNetworkGraphService {

    private final MetroNetworkRepository metroNetworkRepository;

    public MetroNetworkGraphService(MetroNetworkRepository metroNetworkRepository) {
        this.metroNetworkRepository = metroNetworkRepository;
    }

    public Response getEntireNetwork() {
        // TODO: update mapping to d3js graph
        return null;
    }

    public record Response(
            List<Node> nodes,
            List<Link> links
    ) {

        public record Node(String id, String name, String group) {

        }

        public record Link(String source, String target, List<String> lines) {

        }

    }

}
