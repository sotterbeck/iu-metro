package de.sotterbeck.iumetro.usecase.faregate;

import java.util.Optional;

public interface GateRepository {

    Optional<GateDto> findAt(PositionDto position);

}
