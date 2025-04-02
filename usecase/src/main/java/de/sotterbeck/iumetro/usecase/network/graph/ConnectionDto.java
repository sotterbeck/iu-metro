package de.sotterbeck.iumetro.usecase.network.graph;

import java.util.List;
import java.util.UUID;

public record ConnectionDto(
        UUID metroStation1Id,
        UUID metroStation2Id,
        List<String> lineNames
) {

}
