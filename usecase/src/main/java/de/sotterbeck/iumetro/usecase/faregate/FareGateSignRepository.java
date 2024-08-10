package de.sotterbeck.iumetro.usecase.faregate;

import java.util.Optional;

public interface FareGateSignRepository {

    Optional<FareGateDto> findAt(PositionDto location);

}
