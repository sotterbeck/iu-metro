package de.sotterbeck.iumetro.app.faregate;

import de.sotterbeck.iumetro.app.common.PositionDto;

public record GateDto(PositionDto position, String orientation, boolean isOpen) {

}
