package de.sotterbeck.iumetro.usecase.network.graph;

import de.sotterbeck.iumetro.entity.common.Position;
import de.sotterbeck.iumetro.entity.network.StationNode;

import java.util.List;

public class MetroNetworkGraphInteractor {

    private final MetroNetworkRepository metroNetworkRepository;

    public MetroNetworkGraphInteractor(MetroNetworkRepository metroNetworkRepository) {
        this.metroNetworkRepository = metroNetworkRepository;
    }

    public MetroGraphResponseModel getEntireNetwork() {
        MetroNetworkDto networkDto = metroNetworkRepository.getEntireNetwork();

        List<MetroGraphResponseModel.Node> nodes = networkDto.metroStations().stream()
                .map(dto -> {
                    boolean hasConnections = networkDto.connections().stream()
                            .anyMatch(connection -> dto.id().equals(connection.metroStation1Id())
                                    || dto.id().equals(connection.metroStation2Id()));

                    StationNode entity = new StationNode(dto.id().toString(), dto.name(), hasConnections, dto.position()
                            .map(pos -> new Position(pos.x(), pos.y(), pos.z())));

                    return new MetroGraphResponseModel.Node(entity.id(), entity.name(), entity.group());
                })
                .toList();

        List<MetroGraphResponseModel.Link> links = networkDto.connections().stream()
                .map(dto -> new MetroGraphResponseModel.Link(dto.metroStation1Id().toString(), dto.metroStation2Id().toString(), dto.lineNames()))
                .toList();

        return new MetroGraphResponseModel(nodes, links);
    }

}
