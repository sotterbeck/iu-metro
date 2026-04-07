package de.sotterbeck.iumetro.app.faregate;

import de.sotterbeck.iumetro.app.common.PositionDto;

import java.util.Optional;
import java.util.UUID;

public interface PlayerAdapter {

    Optional<PositionDto> findPosition(UUID playerId);

    void teleport(UUID playerId, PositionDto position);

}
