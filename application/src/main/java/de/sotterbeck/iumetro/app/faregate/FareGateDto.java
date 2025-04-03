package de.sotterbeck.iumetro.app.faregate;

import de.sotterbeck.iumetro.app.common.PositionDto;

public record FareGateDto(
        PositionDto location,
        String type,
        String stationId
) {

}
