package de.sotterbeck.iumetro.app.network.line;

import java.util.List;
import java.util.UUID;

public record LineDto(
        String name,
        int color,
        List<UUID> metroStationIds
) {

}
