package de.sotterbeck.iumetro.app.network.graph;

import java.util.UUID;

public record ConnectionDto(
        UUID from,
        UUID to,
        int distance
) {

}
