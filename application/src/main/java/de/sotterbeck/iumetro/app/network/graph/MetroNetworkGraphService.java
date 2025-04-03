package de.sotterbeck.iumetro.app.network.graph;

import de.sotterbeck.iumetro.domain.common.Position;
import de.sotterbeck.iumetro.domain.network.StationNode;

import java.util.List;

public class MetroNetworkGraphService {

    private final MetroNetworkRepository metroNetworkRepository;

    public MetroNetworkGraphService(MetroNetworkRepository metroNetworkRepository) {
        this.metroNetworkRepository = metroNetworkRepository;
    }

    public Response getEntireNetwork() {
        MetroNetworkDto networkDto = metroNetworkRepository.getEntireNetwork();

        List<Response.Node> nodes = networkDto.metroStations().stream()
                .map(dto -> {
                    boolean hasConnections = networkDto.connections().stream()
                            .anyMatch(connection -> dto.id().equals(connection.metroStation1Id())
                                    || dto.id().equals(connection.metroStation2Id()));

                    StationNode entity = new StationNode(dto.id().toString(), dto.name(), hasConnections, dto.position()
                            .map(pos -> new Position(pos.x(), pos.y(), pos.z())));

                    return new Response.Node(entity.id(), entity.name(), entity.group());
                })
                .toList();

        List<Response.Link> links = networkDto.connections().stream()
                .map(dto -> new Response.Link(dto.metroStation1Id().toString(), dto.metroStation2Id().toString(), dto.lineNames()))
                .toList();

        return new Response(nodes, links);
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
