package de.sotterbeck.iumetro.app.faregate;

import de.sotterbeck.iumetro.app.common.PositionDto;

public record FareGateControlRequestModel(PositionDto signPosition, String signOrientation) {

}
