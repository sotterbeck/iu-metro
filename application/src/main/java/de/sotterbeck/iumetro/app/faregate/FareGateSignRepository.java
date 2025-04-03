package de.sotterbeck.iumetro.app.faregate;

import de.sotterbeck.iumetro.app.common.PositionDto;

import java.util.Optional;

public interface FareGateSignRepository {

    Optional<FareGateDto> findAt(PositionDto location);

}
