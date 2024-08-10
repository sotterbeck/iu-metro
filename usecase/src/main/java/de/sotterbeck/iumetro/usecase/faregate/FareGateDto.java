package de.sotterbeck.iumetro.usecase.faregate;

public record FareGateDto(
        PositionDto location,
        String type,
        String stationName
) {

}
