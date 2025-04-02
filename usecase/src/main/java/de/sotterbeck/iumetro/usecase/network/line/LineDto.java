package de.sotterbeck.iumetro.usecase.network.line;

import java.util.List;
import java.util.UUID;

public record LineDto(
        String name,
        int color,
        List<UUID> metroStationIds
) {

}
